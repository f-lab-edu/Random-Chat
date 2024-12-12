package com.example.ranchat.redis.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMatchStatusService {
    @Qualifier("customBooleanRedisTemplate")
    private final RedisTemplate<String, Boolean> redisTemplate;

    public void setMatchStatusTrue(String userId) {
        String key = "userSession:" + userId;
        redisTemplate.opsForHash().put(key, "isMatched", true);
    }

    // 매칭 여부를 확인하기 위한 필드 조회
    public boolean isMatched(String userId) {
        String key = "userSession:" + userId;
        return (boolean) redisTemplate.opsForHash().get(key, "isMatched");
    }

    public void initMatchingStatus(String userId) {
        String key = "userSession:" + userId;
        redisTemplate.opsForHash().put(key, "isMatched", false);
    }
}
