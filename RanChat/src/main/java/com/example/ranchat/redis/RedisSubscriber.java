package com.example.ranchat.redis;

import com.example.ranchat.message.entity.MessageDTO;
import com.example.ranchat.redis.Service.RedisChatRoomService;
import com.example.ranchat.redis.Service.RedisService;
import com.example.ranchat.websocket.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
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

    private final RedisChatRoomService redisChatRoomService;
    private final ObjectMapper objectMapper;
    private final SessionManager sessionManager;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        log.info("body가 어떤 형태이지?: " + body);

        try {
            MessageDTO messageDTO = objectMapper.readValue(body, MessageDTO.class);
            String chatRoomId = messageDTO.getChatRoomId();
            // 레디스에서 채팅방 세션에 있는 유저들을 가져온다. 매칭할 때 이 정보를 만든다.
            List<String> userIds = redisChatRoomService.getUserIds(chatRoomId);
            // userId가 해당 서버에서 접속한 유저라면 웹소켓 세션을 통해 메세지를 발송한다.
            if (userIds != null) {
                for (String userId : userIds) {
                    WebSocketSession session = sessionManager.getSession(userId);
                    if (session != null && session.isOpen()) {
                        synchronized (session) {
                            session.sendMessage(new TextMessage(body));
                        }
                        session.getAttributes().put("chatRoomId", chatRoomId);
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