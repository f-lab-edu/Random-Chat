package com.example.ranchat.chatroom;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSessionInfo {
	private String userId;
	private String webSocketSessionId;
}
