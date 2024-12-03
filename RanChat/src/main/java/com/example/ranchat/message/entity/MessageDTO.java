package com.example.ranchat.message.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private String chatRoomId;
    private String content;
    private String webSocketSessionId;
    private String sender;
    private LocalDateTime timestamp;
}
