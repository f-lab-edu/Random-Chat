package com.example.ranchat.redis.Controller;

import com.example.ranchat.redis.Service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisService redisService;

    // 데이터 저장 엔드포인트
    @PostMapping("/set")
    public ResponseEntity<String> setKeyValue(@RequestParam String key, @RequestParam String value) {
        redisService.set(key, value);
        return ResponseEntity.ok("Key set successfully");
    }

    // 데이터 가져오기 엔드포인트
    @GetMapping("/get")
    public ResponseEntity<Object> getValue(@RequestParam String key) {
        Object value = redisService.get(key);
        if (value != null) {
            return ResponseEntity.ok(value);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 키 삭제 엔드포인트
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteKey(@RequestParam String key) {
        redisService.delete(key);
        return ResponseEntity.ok("Key deleted successfully");
    }

    // Redis 연결 상태 확인 엔드포인트
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        try {
            redisService.set("health_check", "OK");
            Object value = redisService.get("health_check");
            if ("OK".equals(value)) {
                redisService.delete("health_check");
                return ResponseEntity.ok("Redis is working");
            } else {
                return ResponseEntity.status(500).body("Redis is not working as expected");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Redis connection failed: " + e.getMessage());
        }
    }
}