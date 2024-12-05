package com.example.ranchat.chatroom.service;

import com.example.ranchat.chatroom.UserSessionInfo;
import com.example.ranchat.chatroom.dto.MatchingResponseDTO;
import com.example.ranchat.exception.NoMatchingUserException;
import com.example.ranchat.message.entity.MessageDTO;
import com.example.ranchat.redis.RedisPublisher;
import com.example.ranchat.redis.Service.RedisChatRoomService;
import com.example.ranchat.redis.Service.RedisMatchStatusService;
import com.example.ranchat.redis.Service.RedisService;
import com.example.ranchat.response.ResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
    private final RedisService redisService;
    private final RedisPublisher redisPublisher;
    private final ObjectMapper objectMapper;
    private final RedisChatRoomService redisChatRoomService;
    private final RedisMatchStatusService redisMatchStatusService;

    private static final String WAITING_QUEUE = "waitingRoom";



    public MatchingResponseDTO ranChat(String userId) {
        return new MatchingResponseDTO(userId + " is matching");
    }
    @Async
    @Retryable(
            retryFor = NoMatchingUserException.class,
            maxAttempts = 15,
            backoff = @Backoff(delay = 1000))
    public void match(String userId) {
        // 현재 사용자 정보 가져오기, 웹소켓 세션Id를 통해 메세지 전송
        UserSessionInfo currentUserInfo = redisService.getUserSessionInfo(userId);

        // 매칭 시도, 큐에서 하나 뽑기
        String otherUserId = redisService.getUserFromWaitingRoom(WAITING_QUEUE);

        // 상대가 나를 매칭했는지 확인
        if (redisMatchStatusService.isMatched(userId)) {
            // 이미 매칭이 됐으면 꺼낸 유저 다시 대기 큐에 넣기, 이때 빨리 뽑힐 수 있는 방향으로 넣기
            if(otherUserId != null)
                redisService.pushUserToWaitingRoom(WAITING_QUEUE,otherUserId);
            return;

        }

        // 뽑힌 유저가 내가 아니고 null도 아니면 내 매칭 상대다 -> 매칭 시도
        if (otherUserId != null && !otherUserId.equals(userId)) {
            // A,B,C가 있는데 A랑 B가 매칭되면 A가 큐에 남아 있는 상황인데(혹은 B) 이런 경우 C랑 매칭되면 안되니까 필터링 하는 로직
            if (redisMatchStatusService.isMatched(otherUserId)) {
                // 이러면 해당 유저를 큐에 다시 안넣고 자연스럽게 버릴 수 있음.
                log.info("이 유저는 이미 다른 유저와 채팅중입니다.");
                throw new NoMatchingUserException(ResponseCode.NO_WAITING_USER);
            }
            log.info("Matched user {} with user {}.", userId, otherUserId);

            // 매칭된 사용자 정보 가져오기, 레디스에서 가져오니까 다른 서버에서 연결된 웹소켓 유저 정보도 가져옴
            UserSessionInfo otherUserInfo = redisService.getUserSessionInfo(otherUserId);

            if (otherUserInfo != null) {
                // 채팅방 ID 생성
                String chatRoomId = UUID.randomUUID().toString();
                log.info("chatRoomId: " + chatRoomId );
                // 레디스에 저장, 다른 서버에서 레디스에 채팅방Id를 통해 접근해서 매칭된 유저를 다 조회하고, 해당 유저의 Id를 통해 세션에 메세지를 보낼 수 있다.
                redisChatRoomService.saveChatRoomInfo(chatRoomId, userId, otherUserId);
                // 각종 채팅방 관련 엔티티 만들어주기 or JDBC로 만들기, 일단 패스

                // 각 유저의 상태를 매칭 상태로 저장
                makeMatchState(userId, otherUserId);

                // 두 사용자에게 채팅방 입장 메시지 전송
                sendMatchNotification(currentUserInfo, chatRoomId, currentUserInfo.getWebSocketSessionId());
                sendMatchNotification(otherUserInfo, chatRoomId, otherUserInfo.getWebSocketSessionId());

            } else {
                log.warn("User session info not found for user {}", otherUserId);
                throw new NoMatchingUserException(ResponseCode.NO_WAITING_USER);
                // 필요 시 재시도 로직 추가
            }
        } else {
            // 매칭되지 않으면 대기열에 추가
            redisService.addUserToWaitingRoom(WAITING_QUEUE, userId);
            log.info("No match found for user {}. Added back to waiting room.", userId);

            // 적절한 예외 처리, 커스텀 예외 만들어야 할까? 일단 땜빵
            throw new NoMatchingUserException(ResponseCode.NO_WAITING_USER);
        }
    }

    private void makeMatchState(String userId, String otherUserId) {
        redisMatchStatusService.setMatchStatusTrue(userId);
        redisMatchStatusService.setMatchStatusTrue(otherUserId);
    }

    @Recover
    public String recover(NoMatchingUserException e, String userId) {
        log.error("user{}: 매칭 상대를 찾는데 실패했습니다.", userId);
        // 레디스큐에서 자기 정보 빼기
        return "매칭에 실패했습니다. 나중에 다시 시도해주세요";
    }

    private void sendMatchNotification(UserSessionInfo userInfo, String chatRoomId, String websocketSessionId) {
        // 매칭된 사용자에게 채팅방 입장 메시지를 전송합니다.
        // 메시지에 채팅방 ID를 포함하여 전송합니다.(프론트에서 채팅방Id 확인해서 회신 가능하게 해주세요 ㅎ)
        String userId = userInfo.getUserId();
        MessageDTO messageDTO = MessageDTO.builder()
                .chatRoomId(chatRoomId)
                .content(userId + "님이 입장했습니다.")
                .webSocketSessionId(websocketSessionId)
                .sender(userId)
                .timestamp(LocalDateTime.now())
                .build();

        // 레디스에 보내면 다른 서버까지 메세지가 전송됨.
        redisPublisher.publish("chatRoom:" + chatRoomId, messageDTO);
    }

}
