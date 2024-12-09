package com.example.ranchat.redis.Service;

import com.example.ranchat.chatroom.UserSessionInfo;
import com.example.ranchat.chatroom.service.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static com.example.ranchat.chatroom.service.ChatRoomService.WAITING_QUEUE;
import static org.assertj.core.api.Assertions.*;



@SpringBootTest
@ActiveProfiles("test")
public class RedisMatchTest {
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedisService redisService;


    @BeforeEach
    public void dummy() {
        for (int i = 1; i <= 10; i++) {
            UserSessionInfo info = UserSessionInfo.builder().userId(i + "")
                    .webSocketSessionId("websocketSessionId" + i).build();
            redisService.saveUserSessionInfo(info);
            redisTemplate.opsForList().rightPush(WAITING_QUEUE, i + "");
        }
    }

    @DisplayName("10명의 대기 인원이 한번의 API 호출로 다 매칭된다.")
    @Test
    void test() throws InterruptedException {

        //when
        chatRoomService.matchSchedule();

        //then
        assertThat(redisTemplate.opsForList().size(WAITING_QUEUE)).isEqualTo(0);

     }

}
