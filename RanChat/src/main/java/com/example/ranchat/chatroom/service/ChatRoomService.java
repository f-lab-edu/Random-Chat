package com.example.ranchat.chatroom.service;

import com.example.ranchat.chatroom.UserSessionInfo;
import com.example.ranchat.exception.NotFoundUserException;
import com.example.ranchat.message.entity.MatchNotificationDTO;
import com.example.ranchat.redis.RedisPublisher;
import com.example.ranchat.redis.Service.RedisService;
import com.example.ranchat.response.ResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
    private final RedisService redisService;
    private final RedisPublisher redisPublisher;
    private final ObjectMapper objectMapper;

    private static final String WAITING_QUEUE = "waitingRoom";


    @Value("${spring.server.id}")
    private String serverId;

    public String ranChat(String userId) {

        // 현재 사용자 정보 가져오기
        UserSessionInfo currentUserInfo = redisService.getUserSessionInfo(userId);

        // 매칭 시도
        String otherUserId = redisService.getUserFromWaitingRoom(WAITING_QUEUE);

        if (otherUserId != null && !otherUserId.equals(userId)) {

            log.info("Matched user {} with user {}.", userId, otherUserId);

            // 매칭된 사용자 정보 가져오기
            UserSessionInfo otherUserInfo = redisService.getUserSessionInfo(otherUserId);

            if (otherUserInfo != null) {
                // 채팅방 ID 생성
                String chatRoomId = UUID.randomUUID().toString();
                // 레디스에 저장
                redisService.saveUserIdsWithRoomId(chatRoomId, userId, otherUserId);
                // 각종 채팅방 관련 엔티티 만들어주기 or JDBC로 만들기, 일단 패스

                // 두 사용자에게 채팅방 입장 메시지 전송
                sendMatchNotification(currentUserInfo, otherUserInfo, chatRoomId);

            } else {
                log.warn("User session info not found for user {}", otherUserId);
                // 필요 시 재시도 로직 추가
            }
        } else {
            // 매칭되지 않으면 대기열에 추가
            redisService.pushUserToWaitingRoom(WAITING_QUEUE, userId);
            log.info("No match found for user {}. Added back to waiting room.", userId);

            // 적절한 예외 처리, 커스텀 예외 만들어야 할까? 일단 땜빵
            throw new NotFoundUserException(ResponseCode.NO_WAITING_USER);
        }
        return otherUserId;
    }

    private void sendMatchNotification(UserSessionInfo userInfo1, UserSessionInfo userInfo2, String chatRoomId) {
        // 매칭된 사용자에게 채팅방 입장 메시지를 전송합니다.
        // 메시지에 채팅방 ID를 포함하여 전송합니다.(확인용 개발 다 하고 삭제)
        String messageContent = userInfo1.getUserId() + "님이 입장했습니다.\n" + userInfo2.getUserId() + "님이 입장했습니다.";

        MatchNotificationDTO notification = new MatchNotificationDTO("MATCH", chatRoomId, messageContent, userInfo1.getWebSocketSessionId(), userInfo1.getUserId());

        try {
            String message = objectMapper.writeValueAsString(notification);
            redisPublisher.publish("chatRoom:" + chatRoomId, message);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize MatchNotification", e);
            throw new RuntimeException(e);
        }
    }

}
