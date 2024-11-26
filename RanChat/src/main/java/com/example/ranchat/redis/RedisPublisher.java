package com.example.ranchat.redis;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPublisher {

    private final RedisTemplate<String, String> redisTemplate;

    public void publish(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }

    public void publishMessage(String message) {
        // 메시지에서 채널 정보를 추출해야 합니다.
        // 예를 들어, 메시지가 JSON 형식이라면 파싱하여 chatRoomId를 가져옵니다.
        String channel = extractChannelFromMessage(message);
        publish(channel, message);
    }

    private String extractChannelFromMessage(String message) {
        // 메시지를 파싱하여 채널 ID(chatRoomId)를 추출하는 로직을 구현합니다.
        // JSON 파싱을 위해 ObjectMapper 등을 사용할 수 있습니다.

        return "chatRoom:"; //+ parsedChatRoomId;
    }
}
