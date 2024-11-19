package com.example.ranchat.message.entity;

import com.example.ranchat.BaseEntity;
import com.example.ranchat.chatroom.entity.ChatRoom;
import com.example.ranchat.user.entity.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class Message extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Setter
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_key")
    private ChatRoom chatRoom;
    private String chatMessage;

    private LocalDateTime sendTime;
}
