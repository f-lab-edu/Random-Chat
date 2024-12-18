package com.example.ranchat.message.entity;

import java.time.LocalDateTime;

import com.example.ranchat.chatroom.entity.ChatRoom;
import com.example.ranchat.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
	private MessageType type;
	private String chatRoomId;
	private String content;
	private String webSocketSessionId;
	private Long sender;
	private LocalDateTime timestamp;

	public void setNormal() {
		type = MessageType.NORMAL;
	}

	public Message toEntity(User user, ChatRoom chatRoom) {
		return Message.builder()
			.chatMessage(this.getContent())
			.sendTime(this.getTimestamp())
			.user(user)
			.chatRoom(chatRoom)
			.build();
	}

	public void setSendTime(LocalDateTime now) {
		this.timestamp = now;
	}
}
