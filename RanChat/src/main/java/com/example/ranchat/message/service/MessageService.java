package com.example.ranchat.message.service;

import static com.example.ranchat.websocket.ChatWebSocketHandler.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.ranchat.chatroom.entity.ChatRoom;
import com.example.ranchat.chatroom.service.ChatRoomService;
import com.example.ranchat.exception.NotFoundChatRoomException;
import com.example.ranchat.exception.NotFoundUserException;
import com.example.ranchat.message.entity.Message;
import com.example.ranchat.message.entity.MessageDTO;
import com.example.ranchat.message.repository.MessageRepository;
import com.example.ranchat.redis.Service.RedisChatRoomService;
import com.example.ranchat.response.ResponseCode;
import com.example.ranchat.user.entity.User;
import com.example.ranchat.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
	private final MessageRepository messageRepository;
	private final RedisTemplate<String, MessageDTO> redisTemplate;
	private final RedisChatRoomService redisChatRoomService;
	private final UserService userService;
	private final ChatRoomService chatRoomService;
	private final RedissonClient redissonClient;

	public void saveCachedMessages(String chatRoomId) {
		String key = "chatRepository:" + chatRoomId;
		String lockKey = "lock:" + chatRoomId;
		RLock lock = redissonClient.getLock(lockKey);
		try {
			boolean available = lock.tryLock(5, 5, TimeUnit.SECONDS);
			if (!available) {
				return;
			}
			List<MessageDTO> cachedMessages = redisTemplate.opsForList().range(key, 0, CACHE_SIZE - 1);
			LocalDateTime lastMessageTimestamp = redisChatRoomService.getLastMessageTimestamp(chatRoomId);

			if (cachedMessages == null || cachedMessages.isEmpty()) {
				log.info("no message to save");
				return;
			}
			// messageDTO -> message 엔티티 -> DB 저장
			List<Message> messagesToSave = cachedMessages.stream()
				.map(m -> {
					User user = userService.findById(m.getSender()).orElseThrow(() -> new NotFoundUserException(
						ResponseCode.NOT_FOUND_USER));
					ChatRoom chatRoom = chatRoomService.findById(UUID.fromString(m.getChatRoomId())).orElseThrow(() ->
						new NotFoundChatRoomException(ResponseCode.NOT_FOUND_CHATROOM));
					return m.toEntity(user, chatRoom);
				})
				.filter(message -> message.getSendTime().isAfter(lastMessageTimestamp))
				.collect(Collectors.toList());

			if (messagesToSave.isEmpty()) {
				return;
			}
			messageRepository.saveAll(messagesToSave);

			Optional<LocalDateTime> newLastMessageTimestamp = messagesToSave.stream()
				.map(Message::getSendTime)
				.max(LocalDateTime::compareTo);

			LocalDateTime newLastTimestamp = newLastMessageTimestamp.orElse(lastMessageTimestamp);

			// 가장 최근에 저장된 메세지의 timeStamp update
			redisChatRoomService.updateLastMessageTimestamp(chatRoomId, newLastTimestamp);

			// 레디스에서 저장한 내용 삭제
			redisTemplate.opsForList().trim(key, CACHE_SIZE, -1);

		} catch (InterruptedException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		} finally {
			try {
				lock.unlock();
				log.info("unlock complete: " + lock.getName());
			} catch (IllegalMonitorStateException e) {
				log.info("scheduler unlock exception: " + e.getMessage());
			}
		}

	}

	public Boolean checkMessageCacheExist(String chatRoomId) {
		return redisTemplate.hasKey("chatRepository:" + chatRoomId);
	}

	public void deleteMessageCache(String chatRoomId) {
		redisTemplate.delete("chatRepository:" + chatRoomId);
	}
}
