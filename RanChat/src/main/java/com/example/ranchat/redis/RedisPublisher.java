package com.example.ranchat.redis;


import com.example.ranchat.message.entity.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPublisher {

    private final RedisTemplate<String, MessageDTO> redisTemplate;
    public void publish(String chatRoomId, MessageDTO message) {
        String channel = "chatRoom:" + chatRoomId;
        redisTemplate.convertAndSend(channel, message);
    }
    
}
