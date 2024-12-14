package com.example.ranchat.user.entity;

import com.example.ranchat.BaseEntity;
import com.example.ranchat.message.entity.Message;
import com.example.ranchat.userchatroom.entity.UserChatRoom;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "users")
@NoArgsConstructor
@Getter
public class User extends BaseEntity {
    // 기본키 전략 공부 필요
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;

    private String password;

    private String role;

    @OneToMany(mappedBy = "user")
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

    @Builder
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }



    public void addMessage(Message message) {
        messages.add(message);
        message.setUser(this);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        message.setUser(null);
    }
}
