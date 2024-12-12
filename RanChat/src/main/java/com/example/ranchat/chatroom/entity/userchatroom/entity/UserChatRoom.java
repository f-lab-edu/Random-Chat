package com.example.ranchat.chatroom.entity.userchatroom.entity;

import com.example.ranchat.BaseEntity;
import com.example.ranchat.chatroom.entity.ChatRoom;
import com.example.ranchat.user.entity.User;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class UserChatRoom extends BaseEntity {
	@EmbeddedId
	private UserChatRoomId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userKey")
	@JoinColumn(name = "user_key")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("chatRoomKey")
	@JoinColumn(name = "chatroom_key")
	private ChatRoom chatRoom;

}
