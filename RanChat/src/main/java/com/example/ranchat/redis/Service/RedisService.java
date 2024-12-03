package com.example.ranchat.redis.Service;

import com.example.ranchat.chatroom.UserSessionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    @Qualifier("customStringRedisTemplate")
    private final RedisTemplate<String, String> redisTemplate;

    // 대기방에 들어갈 정보를 저장하는 로직
    public void saveUserSessionInfo(UserSessionInfo info) {
        String key = "userSession:" + info.getUserId();
        redisTemplate.opsForHash().put(key,"userId", info.getUserId());
        redisTemplate.opsForHash().put(key,"webSocketSessionId", info.getWebSocketSessionId());
    }

    public void setMatchStatusTrue(String userId) {
        String key = "userSession:" + userId;
        redisTemplate.opsForHash().put(key, "isMatched", "true");
    }

    // 매칭 여부를 확인하기 위한 필드 조회
    public String isMatched(String userId) {
        String key = "userSession:" + userId;
        return (String) redisTemplate.opsForHash().get(key, "isMatched");
    }

    public void deleteUserSessionInfo(String userId) {
        String key = "userSession:" + userId;
        log.info("delete key: " + key);
        redisTemplate.delete(key);
    }

    public UserSessionInfo getUserSessionInfo(String userId) {
        String key = "userSession:" + userId;
        String webSocketSessionId = (String) redisTemplate.opsForHash().get(key, "webSocketSessionId");

        if (webSocketSessionId != null) {
            UserSessionInfo info = new UserSessionInfo();
            info.setUserId(userId);
            info.setWebSocketSessionId(webSocketSessionId);
            return info;
        }
        return null;
    }

    // 대기 방 리스트에 사용자 추가
    public void addUserToWaitingRoom(String queueName, String userId) {
        redisTemplate.opsForList().rightPush(queueName, userId);
    }

    // 대기 방 리스트에서 사용자 가져오기 (LPOP)
    public String getUserFromWaitingRoom(String queueName) {
        return redisTemplate.opsForList().leftPop(queueName);
    }


    // 대기 방 리스트에 사용자 다시 추가 (LPUSH)
    public void pushUserToWaitingRoom(String queueName, String userId) {
        redisTemplate.opsForList().leftPush(queueName, userId);
    }
}
