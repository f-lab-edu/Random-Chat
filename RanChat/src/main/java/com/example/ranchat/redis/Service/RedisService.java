package com.example.ranchat.redis.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    // 데이터 저장
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 데이터 가져오기
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 키 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
