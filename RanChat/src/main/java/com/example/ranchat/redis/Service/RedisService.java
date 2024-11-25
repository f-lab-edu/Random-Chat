package com.example.ranchat.redis.Service;

import com.example.ranchat.chatroom.UserSessionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    // 대기방에 들어갈 정보를 저장하는 로직
    public void saveUserSessionInfo(UserSessionInfo info) {
        String key = "userSession:" + info.getUserId();
        redisTemplate.opsForHash().put(key,"userId", info.getUserId());
        redisTemplate.opsForHash().put(key,"webSocketSessionId", info.getWebSocketSessionId());
        redisTemplate.opsForHash().put(key, "serverId", info.getServerId());
    }

    public void deleteUserSessionInfo(String userId) {
        String key = "userSession:" + userId;
        log.info("delete key: " + key);
        redisTemplate.delete(key);
    }

    public UserSessionInfo getUserSessionInfo(String userId) {
        String key = "userSession:" + userId;
        String webSocketSessionId = (String) redisTemplate.opsForHash().get(key, "webSocketSessionId");
        String serverId = (String) redisTemplate.opsForHash().get(key, "serverId");

        if (webSocketSessionId != null && serverId != null) {
            UserSessionInfo info = new UserSessionInfo();
            info.setUserId(userId);
            info.setWebSocketSessionId(webSocketSessionId);
            info.setServerId(serverId);
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

    public Long getUserTimestamp(String userId) {
        String ts = (String) redisTemplate.opsForHash().get("waitingTimestamp", userId);
        return ts != null ? Long.parseLong(ts) : null;
    }

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
