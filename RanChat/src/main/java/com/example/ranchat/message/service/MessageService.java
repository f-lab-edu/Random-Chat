package com.example.ranchat.message.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.ranchat.chatroom.entity.ChatRoom;
import com.example.ranchat.chatroom.service.ChatRoomService;
import com.example.ranchat.message.entity.Message;
import com.example.ranchat.message.entity.MessageDTO;
import com.example.ranchat.message.repository.MessageRepository;
import com.example.ranchat.redis.Service.RedisChatRoomService;
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

	public void saveCachedMessages(String chatRoomId) {
		String key = "chatRepository:" + chatRoomId;
		Long size = redisTemplate.opsForList().size(key);

		List<MessageDTO> cachedMessages = redisTemplate.opsForList().range(key, 0, size - 1);
		LocalDateTime lastMessageTimestamp = redisChatRoomService.getLastMessageTimestamp(chatRoomId);

		if (cachedMessages == null || cachedMessages.isEmpty()) {
			log.info("no message to save");
			return;
		}
		// messageDTO -> message 엔티티 -> DB 저장
		List<Message> messagesToSave = cachedMessages.stream()
			.map(m -> {
				Optional<User> user = userService.findById(m.getSender());
				Optional<ChatRoom> byId = chatRoomService.findById(UUID.fromString(m.getChatRoomId()));
				ChatRoom chatRoom = byId.get();

				return m.toEntity(user.get(), chatRoom);
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

	}
}
