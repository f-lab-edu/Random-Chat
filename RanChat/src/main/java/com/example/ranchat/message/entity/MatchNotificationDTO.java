package com.example.ranchat.message.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchNotificationDTO {
    private String type;
    private String chatRoomId;
    private String content;
    private String webSocketSessionId;
    private String userId;
}
