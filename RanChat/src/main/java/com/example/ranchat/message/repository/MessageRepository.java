package com.example.ranchat.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ranchat.message.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
