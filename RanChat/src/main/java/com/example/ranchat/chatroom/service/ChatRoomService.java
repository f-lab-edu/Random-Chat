package com.example.ranchat.chatroom.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ranchat.chatroom.dto.MatchingResponseDTO;
import com.example.ranchat.chatroom.entity.ChatRoom;
import com.example.ranchat.chatroom.repository.ChatRoomRepository;
import com.example.ranchat.redis.Service.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatRoomService {
	private final RedisService redisService;
	private final RedissonClient redissonClient;
	private final ChatRoomRepository chatRoomRepository;

	public static final String WAITING_QUEUE = "waitingRoom";
	private static final String LOCK = "matchingLock";

	public MatchingResponseDTO ranChat(String userId) {
		redisService.addUserToWaitingRoom(WAITING_QUEUE, userId);
		return new MatchingResponseDTO(userId + " is matching");
	}

	@Scheduled(fixedDelay = 3000)
	@Transactional
	public void matchSchedule() {
		RLock lock = redissonClient.getLock(LOCK);

		// redisson으로 락 획득 -> 동기화
		try {
			boolean available = lock.tryLock(5, 5, TimeUnit.SECONDS);
			if (!available) {
				return;
			}
			Long size = redisService.checkQueueSize(WAITING_QUEUE);
			// 대기 유저가 충분히 있으면 for문 순회하면서 매칭시켜줌.
			log.info("test size: " + size);
			if (size >= 2) {
				for (int i = 0; i < size / 2; i++) {
					String firstUser = redisService.getUserFromWaitingRoom(WAITING_QUEUE);
					String secondUser = redisService.getUserFromWaitingRoom(WAITING_QUEUE);
					// 비동기적으로 매칭을 수행하고, 바로 다음 for문 돌며 채팅 매칭
					ChatRoom chatRoom = createChatRoom();
					redisService.match(firstUser, secondUser, chatRoom);
				}
			} else {
				log.info("Not enough for matching");
			}
		} catch (InterruptedException e) {
			log.info(e.getMessage());
			throw new RuntimeException(e);
		} finally {
			try {
				lock.unlock();
				log.info("unlock complete: " + lock.getName());
			} catch (IllegalMonitorStateException e) {
				// 이미 종료된 락일 때 발생함. 이 경우에는 ExceptionHandler로 굳이 잡을 필요는 없으니 여기서 log만 찍자
				log.info("scheduler unlock exception: " + e.getMessage());
			}
		}

		// 매칭 해주기, 입장 메시지, 매칭 상태 변환
		// 문제는 매칭 완료했는데, 상대가 웹소켓 연결 해제했어 -> 채팅방 나가는 API

	}

	private ChatRoom createChatRoom() {
		ChatRoom chatRoom = ChatRoom.builder().build();
		chatRoomRepository.save(chatRoom);
		return chatRoom;
	}

	public Optional<ChatRoom> findById(UUID chatRoomId) {
		return chatRoomRepository.findById(chatRoomId);
	}

}
