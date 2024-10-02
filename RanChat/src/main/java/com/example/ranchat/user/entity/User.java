package com.example.ranchat.user.entity;

import com.example.ranchat.BaseEntity;
import com.example.ranchat.user.dto.JoinDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
public class User extends BaseEntity {
    // 기본키 전략 공부 필요
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;

    private String password;

    private String role;

    @Builder
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }





}
