package com.example.ranchat.redis;

import com.example.ranchat.message.entity.MatchNotificationDTO;
import com.example.ranchat.redis.Service.RedisService;
import com.example.ranchat.websocket.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final SessionManager sessionManager;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        log.info("body가 어떤 형태이지?: " + body);
        log.info("Received message from channel {}: {}", channel, body);

        try {
            MatchNotificationDTO notification = objectMapper.readValue(body, MatchNotificationDTO.class);
            String chatRoomId = notification.getChatRoomId();
            List<String> userIds = redisService.getUserIds(chatRoomId);
            if (!userIds.isEmpty()) {
                for (String userId : userIds) {
                    WebSocketSession session = sessionManager.getSession(userId);
                    if (session != null && session.isOpen()) {
                        session.sendMessage(new TextMessage(body));
                        log.info("웹소켓에 메시지 전송");
                    } else {
                        log.warn("활성화된 웹소켓 세션이 없다.");
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage() + ": onMessage에서 에러");
        }



    }

    private String extractRecipientFromMessage(String message) {
        // 메시지를 파싱하여 수신자 ID를 추출하는 로직을 구현합니다.
        return null;//parsedRecipientUserId;
    }
}