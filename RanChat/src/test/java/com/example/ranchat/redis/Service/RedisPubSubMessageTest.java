package com.example.ranchat.redis.Service;

import com.example.ranchat.chatroom.UserSessionInfo;
import com.example.ranchat.message.entity.MessageDTO;
import com.example.ranchat.message.entity.MessageType;
import com.example.ranchat.redis.RedisPublisher;
import com.example.ranchat.redis.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class RedisPubSubMessageTest {
    @Autowired
    private  RedisPublisher redisPublisher;
    @MockBean
    private  RedisSubscriber redisSubscriber;

    @Test
    @DisplayName("Redis의 Pub/Sub를 통해 입장 메세지 전달을 확인")
    public void sendEnterMessage() throws InterruptedException {
        // given
        UserSessionInfo info = UserSessionInfo.builder()
                .userId("1")
                .webSocketSessionId("websocketSession1")
                .build();

        String chatRoomId = "chatRoom1";

        LocalDateTime now = LocalDateTime.now();
        MessageDTO messageDTO = MessageDTO.builder()
                .type(MessageType.ENTER)
                .chatRoomId(chatRoomId)
                .content(info.getUserId() + "님이 입장했습니다.")
                .webSocketSessionId(info.getWebSocketSessionId())
                .sender(info.getUserId())
                .timestamp(now)
                .build();
        // when
        redisPublisher.publish(chatRoomId, messageDTO);
        Thread.sleep(1000);
        // then
        verify(redisSubscriber, timeout(1000)).handleMessage(any(MessageDTO.class));

        ArgumentCaptor<MessageDTO> messageCaptor = ArgumentCaptor.forClass(MessageDTO.class);
        verify(redisSubscriber, timeout(1000)).handleMessage(messageCaptor.capture());

        MessageDTO capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getType()).isEqualTo(MessageType.ENTER);
        assertThat(capturedMessage.getChatRoomId()).isEqualTo(chatRoomId);
        assertThat(capturedMessage.getContent()).isEqualTo(info.getUserId() + "님이 입장했습니다.");
        assertThat(capturedMessage.getWebSocketSessionId()).isEqualTo(info.getWebSocketSessionId());
        assertThat(capturedMessage.getSender()).isEqualTo(info.getUserId());
        assertThat(capturedMessage.getTimestamp()).isEqualTo(now);

    }


}
