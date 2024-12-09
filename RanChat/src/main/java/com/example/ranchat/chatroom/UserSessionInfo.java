package com.example.ranchat.chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class UserSessionInfo {
    private String userId;
    private String webSocketSessionId;
}
