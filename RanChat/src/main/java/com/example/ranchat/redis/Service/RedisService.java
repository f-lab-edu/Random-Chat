package com.example.ranchat.redis.Service;

import com.example.ranchat.chatroom.UserSessionInfo;
import com.example.ranchat.message.entity.MessageDTO;
import com.example.ranchat.message.entity.MessageType;
import com.example.ranchat.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {


    @Qualifier("customStringRedisTemplate")
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisChatRoomService redisChatRoomService;
    private final RedisMatchStatusService redisMatchStatusService;
    private final RedisPublisher redisPublisher;


    @Async
    public void match(String userAId, String userBId) {
        // 매칭된 사용자 정보 가져오기, 레디스에서 가져오니까 다른 서버에서 연결된 웹소켓 유저 정보도 가져옴
        UserSessionInfo userAInfo = getUserSessionInfo(userAId);
        UserSessionInfo userBInfo = getUserSessionInfo(userBId);

        String chatRoomId = UUID.randomUUID().toString();
        log.info("chatRoomId: " + chatRoomId );
        // 레디스에 저장, 다른 서버에서 레디스에 채팅방Id를 통해 접근해서 매칭된 유저를 다 조회하고, 해당 유저의 Id를 통해 세션에 메세지를 보낼 수 있다.
        redisChatRoomService.saveChatRoomInfo(chatRoomId, userAId, userBId);
        // 각종 채팅방 관련 엔티티 만들어주기 or JDBC로 만들기, 일단 패스

        // 각 유저의 상태를 매칭 상태로 저장
        makeMatchState(userAId, userBId);

        // 두 사용자에게 채팅방 입장 메시지 전송
        sendEnterMessage(userAInfo, chatRoomId);
        sendEnterMessage(userBInfo, chatRoomId);
    }

    private void makeMatchState(String userId, String otherUserId) {
        redisMatchStatusService.setMatchStatusTrue(userId);
        redisMatchStatusService.setMatchStatusTrue(otherUserId);
    }

    private void sendEnterMessage(UserSessionInfo userInfo, String chatRoomId) {
        // 매칭된 사용자에게 채팅방 입장 메시지를 전송합니다.
        // 메시지에 채팅방 ID를 포함하여 전송합니다.(프론트에서 채팅방Id 확인해서 회신 가능하게 해주세요 ㅎ)
        String userId = userInfo.getUserId();
        MessageDTO messageDTO = MessageDTO.builder()
                .type(MessageType.ENTER)
                .chatRoomId(chatRoomId)
                .content(userId + "님이 입장했습니다.")
                .webSocketSessionId(userInfo.getWebSocketSessionId())
                .sender(userId)
                .timestamp(LocalDateTime.now())
                .build();

        // 레디스에 보내면 다른 서버까지 메세지가 전송됨.
        redisPublisher.publish("chatRoom:" + chatRoomId, messageDTO);
    }



    // 대기방에 들어갈 정보를 저장하는 로직
    public void saveUserSessionInfo(UserSessionInfo info) {
        String key = "userSession:" + info.getUserId();
        redisTemplate.opsForHash().put(key,"userId", info.getUserId());
        redisTemplate.opsForHash().put(key,"webSocketSessionId", info.getWebSocketSessionId());
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
            UserSessionInfo info = UserSessionInfo.builder()
                    .userId(userId)
                    .webSocketSessionId(webSocketSessionId)
                    .build();
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

    public Long checkQueueSize(String queueName) {
        return redisTemplate.opsForList().size(queueName);
    }
}
