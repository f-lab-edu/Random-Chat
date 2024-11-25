package com.example.ranchat.chatroom;

import lombok.Data;

@Data
public class UserSessionInfo {
    private String userId;
    private String webSocketSessionId;
    private String serverId;
}
