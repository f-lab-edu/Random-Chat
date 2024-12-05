package com.example.ranchat.chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSessionInfo {
    private String userId;
    private String webSocketSessionId;
}
