package com.example.ranchat.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    // 웹소켓 연결 시 데이터 추가, 웹소켓에 메세지를 보내는 역할
    private final ConcurrentHashMap<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    // 매칭 상태 관리, 매칭 재시도 시 자신의 정보를 확인해서 재시도 여부 결정

    public void addSession(String userId, WebSocketSession session) {
        userSessions.put(userId, session);
    }

    public void removeSession(String userId) {
        userSessions.remove(userId);
    }

    public WebSocketSession getSession(String userId) {
        return userSessions.get(userId);
    }

}
