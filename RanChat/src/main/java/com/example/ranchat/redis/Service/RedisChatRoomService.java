package com.example.ranchat.redis.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisChatRoomService {
    @Qualifier("customStringRedisTemplate")
    private final RedisTemplate<String, String> redisTemplate;

    public void saveChatRoomInfo(String chatRoomId, String userId1, String userId2) {
        redisTemplate.opsForHash().put(chatRoomId, "userId1", userId1);
        redisTemplate.opsForHash().put(chatRoomId, "userId2", userId2);
    }

    public void deleteChatRoomInfo(String chatRoomId) {
        redisTemplate.delete(chatRoomId);
    }

    public List<String> getUserIds(String chatRoomId) {
        String userId1 = (String)redisTemplate.opsForHash().get(chatRoomId, "userId1");
        String userId2 = (String)redisTemplate.opsForHash().get(chatRoomId, "userId2");
        if (userId1 != null && userId2 != null) {
            return List.of(userId1, userId2);
        }
        return null;
    }
}
