package com.example.ranchat.websocket;

import com.example.ranchat.chatroom.UserSessionInfo;
import com.example.ranchat.message.entity.MessageDTO;
import com.example.ranchat.redis.RedisPublisher;
import com.example.ranchat.redis.Service.RedisChatRoomService;
import com.example.ranchat.redis.Service.RedisMatchStatusService;
import com.example.ranchat.redis.Service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler implements WebSocketHandler {

    private final RedisService redisService;
    private final RedisPublisher redisPublisher;
    private final SessionManager sessionManager;
    private final ObjectMapper objectMapper;
    private final RedisChatRoomService redisChatRoomService;
    private final RedisMatchStatusService redisMatchStatusService;


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


        redisService.saveUserSessionInfo(sessionInfo);
        // boolean 값 처리를 위해 분리
        redisMatchStatusService.initMatchingStatus(userId);

        // 로컬 맵에 세션 저장
        sessionManager.addSession(userId, session);

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // 메시지 처리 로직 구현
        String payload = (String) message.getPayload();
        String chatRoomId = getChatRoomIdFromMessage(payload);

        // 채팅 메시지를 Redis에 발행
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
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws IOException {
        // 세션 종료 시 처리 로직
        String userId = getUserIdFromSession(session);

        // Redis에서 세션 정보 삭제
        if (userId != null) {
            String chatRoomId = (String) session.getAttributes().get("chatRoomId");
            // 웹소켓 연결만 된 경우
            if (chatRoomId == null) {
                log.info("채팅방 연결을 아직 안했기 때문에 레디스에서 세션만 제거합니다.");
                redisService.deleteUserSessionInfo(userId);
                // 로컬 맵에서 세션 삭제
                sessionManager.removeSession(userId);
                return;
            }
            String exitMessage = makeExitMessage(userId);

            List<String> userIds = redisChatRoomService.getUserIds(chatRoomId);
            // A랑B가 채팅 중 -> A 웹소켓 종료 -> 레디스 비움 -> B는 종료할 때 userIds == null
            if(userIds != null) {
                for (String id : userIds) {
                    if (id.equals(userId)) {
                        continue;
                    }
                    // 상대에게 퇴장 메세지 보내기
                    WebSocketSession friendSession = sessionManager.getSession(id);
                    friendSession.sendMessage(new TextMessage(exitMessage));
                }
            }

            redisService.deleteUserSessionInfo(userId);
            // 로컬 맵에서 세션 삭제
            sessionManager.removeSession(userId);

            redisChatRoomService.deleteChatRoomInfo(chatRoomId);


            log.info("User disconnected: {}", userId);
        }


    }

    private String makeExitMessage(String userId) {
        MessageDTO exitMessage = MessageDTO.builder()
                .content(userId + "님이 퇴장합니다")
                .timestamp(LocalDateTime.now())
                .build();
        try {
            String message = objectMapper.writeValueAsString(exitMessage);
            return message;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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
            JsonNode node = objectMapper.readTree(message);
            return node.has("chatRoomId") ? node.get("chatRoomId").asText() : null;
        } catch (JsonProcessingException e) {
            log.error("Failed to extract chatRoomId from message", e);
            return null;
        }
    }
}