package com.example.ranchat.message.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
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
