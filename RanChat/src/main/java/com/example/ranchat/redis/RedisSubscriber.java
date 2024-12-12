package com.example.ranchat.redis;

import com.example.ranchat.message.entity.MessageDTO;
import com.example.ranchat.redis.Service.RedisChatRoomService;
import com.example.ranchat.websocket.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber {

    private final RedisChatRoomService redisChatRoomService;
    private final ObjectMapper objectMapper;
    private final SessionManager sessionManager;
    private final ConcurrentHashMap<String, ReentrantLock> sessionLocks = new ConcurrentHashMap<>();

    public void handleMessage(MessageDTO message) {

        try {
            String chatRoomId = message.getChatRoomId();
            // 레디스에서 채팅방 세션에 있는 유저들을 가져온다. 매칭할 때 이 정보를 만든다.
            List<String> userIds = redisChatRoomService.getUserIds(chatRoomId);
            // userId가 해당 서버에서 접속한 유저라면 웹소켓 세션을 통해 메세지를 발송한다.
            if (userIds != null) {
                for (String userId : userIds) {
                    WebSocketSession session = sessionManager.getSession(userId);
                    if (session != null && session.isOpen()) {
                        String sessionId = session.getId();
                        ReentrantLock lock = sessionLocks.computeIfAbsent(sessionId, id -> new ReentrantLock());

                        lock.lock();
                        try{
                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                        }finally {
                            lock.unlock();
                        }
                        session.getAttributes().put("chatRoomId", chatRoomId);
                        log.info("웹소켓에 메시지 전송");
                    } else {
                        log.warn("활성화된 웹소켓 세션이 없다.");
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeLock(String sessionId) {
        sessionLocks.remove(sessionId);
    }

}