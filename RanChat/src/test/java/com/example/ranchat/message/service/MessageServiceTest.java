package com.example.ranchat.message.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.example.ranchat.chatroom.entity.ChatRoom;
import com.example.ranchat.chatroom.service.ChatRoomService;
import com.example.ranchat.message.entity.Message;
import com.example.ranchat.message.entity.MessageDTO;
import com.example.ranchat.message.repository.MessageRepository;
import com.example.ranchat.redis.Service.RedisChatRoomService;
import com.example.ranchat.user.entity.User;
import com.example.ranchat.user.service.UserService;

@ActiveProfiles("test")
class MessageServiceTest {
	@InjectMocks
	private MessageService messageService;

	@Mock
	private MessageRepository messageRepository;

	@Mock
	private RedisTemplate<String, MessageDTO> redisTemplate;

	@Mock
	private RedisChatRoomService redisChatRoomService;

	@Mock
	private UserService userService;

	@Mock
	private ChatRoomService chatRoomService;

	@Mock
	private ListOperations<String, MessageDTO> listOperations;
	String chatRoomId = "fce280fc-dcb7-4736-805f-8eb92587fd93";
	String key = "chatRepository:" + chatRoomId;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		when(redisTemplate.opsForList()).thenReturn(listOperations);
	}

	@Test
	@DisplayName("캐시에 메세지가 없을 때 동작 테스트")
	public void testSaveCachedMessagesWhenNoCachedMessages() {
		when(listOperations.size(key)).thenReturn(0L);
		when(listOperations.range(key, 0, -1)).thenReturn(null);

		messageService.saveCachedMessages(chatRoomId);

		verify(messageRepository, never()).saveAll(any());
	}

	@Test
	void testSaveCachedMessagesWhenCachedMessages() {
		// Redis에서 가져올 메시지 리스트 생성
		MessageDTO dto1 = MessageDTO.builder()
			.sender(1L)
			.chatRoomId(chatRoomId)
			.content("Hello").timestamp(LocalDateTime.now().minusMinutes(10))
			.build();

		MessageDTO dto2 = MessageDTO.builder()
			.sender(2L)
			.chatRoomId(chatRoomId)
			.content("Hi").timestamp(LocalDateTime.now().minusMinutes(5))
			.build();

		List<MessageDTO> cachedMessages = Arrays.asList(dto1, dto2);
		when(listOperations.size(key)).thenReturn(2L);
		when(listOperations.range(key, 0, 1)).thenReturn(cachedMessages);

		// 마지막 메시지 타임스탬프
		LocalDateTime lastTimestamp = LocalDateTime.now().minusMinutes(15);
		when(redisChatRoomService.getLastMessageTimestamp(chatRoomId)).thenReturn(lastTimestamp);

		User user1 = User.builder()
			.username("user1")
			.build();
		when(userService.findById(1)).thenReturn(Optional.ofNullable(user1));

		User user2 = User.builder()
			.username("user2")
			.build();
		when(userService.findById(2)).thenReturn(Optional.ofNullable(user2));

		ChatRoom chatRoom = ChatRoom.builder()
			.chatRoomId(UUID.fromString("fce280fc-dcb7-4736-805f-8eb92587fd93"))
			.build();
		when(chatRoomService.findById(UUID.fromString(chatRoomId))).thenReturn(Optional.of(chatRoom));

		messageService.saveCachedMessages(chatRoomId);

		// 메시지가 저장되었는지 확인
		ArgumentCaptor<List<Message>> captor = ArgumentCaptor.forClass(List.class);
		verify(messageRepository, times(1)).saveAll(captor.capture());

		List<Message> savedMessages = captor.getValue();
		assertEquals(2, savedMessages.size());
		assertEquals("Hello", savedMessages.get(0).getChatMessage());
		assertEquals("Hi", savedMessages.get(1).getChatMessage());

		// 마지막 타임스탬프 업데이트 확인
		verify(redisChatRoomService, times(1)).updateLastMessageTimestamp(eq(chatRoomId), any(LocalDateTime.class));

	}

	@Test
	void testSaveCachedMessagesWhenNoNewMessages() {
		// Redis에서 가져올 메시지 리스트 생성
		MessageDTO dto1 = MessageDTO.builder()
			.sender(1L)
			.chatRoomId(chatRoomId)
			.content("Hello").timestamp(LocalDateTime.now().minusMinutes(10))
			.build();

		MessageDTO dto2 = MessageDTO.builder()
			.sender(2L)
			.chatRoomId(chatRoomId)
			.content("Hi").timestamp(LocalDateTime.now().minusMinutes(5))
			.build();

		List<MessageDTO> cachedMessages = Arrays.asList(dto1, dto2);
		when(listOperations.size(key)).thenReturn(2L);
		when(listOperations.range(key, 0, 1)).thenReturn(cachedMessages);

		// 마지막 메시지 타임스탬프 -> 가장 최신 시간
		LocalDateTime lastTimestamp = LocalDateTime.now();
		when(redisChatRoomService.getLastMessageTimestamp(chatRoomId)).thenReturn(lastTimestamp);

		User user1 = User.builder()
			.username("user1")
			.build();
		when(userService.findById(1)).thenReturn(Optional.ofNullable(user1));

		User user2 = User.builder()
			.username("user2")
			.build();
		when(userService.findById(2)).thenReturn(Optional.ofNullable(user2));

		ChatRoom chatRoom = ChatRoom.builder()
			.chatRoomId(UUID.fromString("fce280fc-dcb7-4736-805f-8eb92587fd93"))
			.build();
		when(chatRoomService.findById(UUID.fromString(chatRoomId))).thenReturn(Optional.of(chatRoom));

		messageService.saveCachedMessages(chatRoomId);

		verify(messageRepository, never()).saveAll(any());
	}

}