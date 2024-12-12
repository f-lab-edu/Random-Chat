package com.example.ranchat.redis.Service;

import com.example.ranchat.chatroom.UserSessionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest()
class RedisServiceTest {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisChatRoomService redisChatRoomService;
    @Autowired
    private RedisMatchStatusService redisMatchStatusService;

    @BeforeEach
    public void cleanUpRedis() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }


    @Test
    @DisplayName("유저 세션 정보 저장 테스트")
    public void saveUserInfoSessionTest() {
        // given
        UserSessionInfo info = UserSessionInfo.builder()
                .userId("1")
                .webSocketSessionId("websocketSession1")
                .build();

        String key = "userSession:" + info.getUserId();


        // when
        redisService.saveUserSessionInfo(info);

        // then
        assertThat(redisTemplate.opsForHash().get(key, "userId"))
                .isEqualTo("1");
        assertThat(redisTemplate.opsForHash().get(key, "webSocketSessionId"))
                .isEqualTo("websocketSession1");

    }

    @Test
    @DisplayName("유저 세션 정보 삭제 테스트")
    public void deleteUserSessionInfoTest() {
        // given
        UserSessionInfo info = UserSessionInfo.builder()
                .userId("1")
                .webSocketSessionId("websocketSession1")
                .build();

        String key = "userSession:" + info.getUserId();

        // when
        redisService.saveUserSessionInfo(info);
        redisService.deleteUserSessionInfo(info.getUserId());

        // then
        assertThat(redisTemplate.opsForHash().get(key, "userId"))
                .isEqualTo(null);
        assertThat(redisTemplate.opsForHash().get(key, "webSocketSessionId"))
                .isEqualTo(null);
    }

    @Test
    @DisplayName("유저 세션 정보 조회 테스트")
    public void getUserSessionInfoTest() {
        // given
        UserSessionInfo info = UserSessionInfo.builder()
                .userId("1")
                .webSocketSessionId("websocketSession1")
                .build();

        String key = "userSession:" + info.getUserId();

        // when
        redisService.saveUserSessionInfo(info);
        UserSessionInfo userSessionInfo = redisService.getUserSessionInfo(info.getUserId());

        // then
        assertThat(userSessionInfo.getUserId()).isEqualTo("1");
        assertThat(userSessionInfo.getWebSocketSessionId()).isEqualTo("websocketSession1");
    }

    @Test
    @DisplayName("대기큐에 삽입, 조회, 사이즈 조회 테스트")
    public void waitingRoomTest() {
        // given
        String queueName = "waitingRoom";
        String userId1 = "user1";
        String userId2 = "user2";

        // when
        // 사용자 추가
        redisService.addUserToWaitingRoom(queueName, userId1);
        redisService.addUserToWaitingRoom(queueName, userId2);

        Long sizeAfterPush = redisService.checkQueueSize(queueName);


        String retrievedUser = redisService.getUserFromWaitingRoom(queueName);
        Long sizeAfterPop = redisService.checkQueueSize(queueName);

        // then
        // 두 명이 큐에 들어감
        assertThat(sizeAfterPush).isEqualTo(2);
        // 먼저 들어간 유저가 먼저 나옴(FIFO)
        assertThat(retrievedUser).isEqualTo(userId1);
        assertThat(sizeAfterPop).isEqualTo(1);

    }

    @Test
    @DisplayName("유저 매칭 상태 변경 메서드 테스트")
    public void makeMatchState() {
        // given
        UserSessionInfo info = UserSessionInfo.builder()
                .userId("1")
                .webSocketSessionId("websocketSession1")
                .build();

        // when
        redisService.saveUserSessionInfo(info);
        redisMatchStatusService.setMatchStatusTrue(info.getUserId());

        // then
        assertThat(redisMatchStatusService.isMatched(info.getUserId())).isEqualTo(true);

    }

    @Test
    @DisplayName("채팅방 세션 정보 생성 확인 테스트")
    public void saveChatRoomInfo() {
        // given
        String chatRoomId = "chatRoom1";
        String userId1 = "user1";
        String userId2 = "user2";

        // when
        redisChatRoomService.saveChatRoomInfo(chatRoomId, userId1, userId2);
        List<String> userIds = redisChatRoomService.getUserIds(chatRoomId);

        // then
        assertThat(userIds.size()).isEqualTo(2);
        assertThat(userIds).contains("user1").contains("user2");

    }


}