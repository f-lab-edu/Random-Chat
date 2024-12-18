package com.example.ranchat.userchatroom.service;

import org.springframework.stereotype.Service;

import com.example.ranchat.chatroom.entity.ChatRoom;
import com.example.ranchat.user.entity.User;
import com.example.ranchat.userchatroom.entity.UserChatRoom;
import com.example.ranchat.userchatroom.repository.UserChatRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserChatRoomService {
	private final UserChatRoomRepository userChatRoomRepository;

	public void createUserChatRoom(ChatRoom chatRoom, User user) {
		UserChatRoom userChatRoom = UserChatRoom.builder()
			.chatRoom(chatRoom)
			.user(user)
			.build();
		userChatRoomRepository.save(userChatRoom);
	}
}
