package com.example.ranchat.websocket;

import com.example.ranchat.chatroom.UserSessionInfo;
import com.example.ranchat.redis.RedisPublisher;
import com.example.ranchat.redis.RedisSubscriber;
import com.example.ranchat.redis.Service.RedisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.log.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import java.util.concurrent.ConcurrentHashMap;


@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler implements WebSocketHandler {

    private final RedisService redisService;
    private final RedisPublisher redisPublisher;
    private final SessionManager sessionManager;

    @Value("${spring.server.id}")
    private String serverId;

    // 사용자 ID와 세션의 매핑을 관리하는 맵

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 사용자 ID를 세션에서 추출하거나 핸드셰이크 시 전달된 정보를 통해 가져옵니다.
        String sessionId = session.getId();
        String userId = getUserIdFromSession(session);
        if (userId == null) {
            log.error("no userId");
        }
        log.info("userId: " + userId);

        // 세션 정보를 Redis에 저장
        UserSessionInfo sessionInfo = new UserSessionInfo();
        sessionInfo.setUserId(userId);
        sessionInfo.setWebSocketSessionId(sessionId);
        sessionInfo.setServerId(serverId);

        redisService.saveUserSessionInfo(sessionInfo);

        // 로컬 맵에 세션 저장
        sessionManager.addSession(userId, session);

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // 메시지 처리 로직 구현
        String payload = message.getPayload().toString();

        // 메시지를 파싱하여 액션을 결정합니다.
        // 예: 채팅 메시지 전송, 매칭 요청 등
        // 여기서는 채팅 메시지라고 가정하고 처리합니다.

        // 채팅 메시지를 Redis에 발행
        String chatRoomId = getChatRoomIdFromMessage(payload);
        if (chatRoomId != null) {
            redisPublisher.publish("chatRoom:" + chatRoomId, payload);
        } else {
            log.warn("Invalid message format: {}", payload);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // 에러 처리 로직
        log.error("Transport error in session {}", session.getId(), exception);
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        // 세션 종료 시 처리 로직
        String sessionId = session.getId();
        String userId = getUserIdFromSession(session);

        // Redis에서 세션 정보 삭제
        if (userId != null) {
            redisService.deleteUserSessionInfoBySessionId(userId);
            // 로컬 맵에서 세션 삭제
            sessionManager.removeSession(userId);

            log.info("User disconnected: {}", userId);
        }


    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    //ws://localhost:8080/ws/chat&userId=1
    private String getUserIdFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "userId".equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }
    private String getChatRoomIdFromMessage(String message) {
        // 메시지에서 chatRoomId를 추출하는 로직을 구현합니다.
        // 예를 들어, JSON 메시지라면 파싱하여 chatRoomId를 가져옵니다.
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(message);
            return node.has("chatRoomId") ? node.get("chatRoomId").asText() : null;
        } catch (Exception e) {
            log.error("Failed to extract chatRoomId from message", e);
            return null;
        }
    }
}