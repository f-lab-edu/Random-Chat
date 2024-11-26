package com.example.ranchat.chatroom.controller;


public class RanChatResponse {
    private String message;
    private String chatRoomId;

    // 기본 생성자
    public RanChatResponse() {}

    // 매개변수가 있는 생성자
    public RanChatResponse(String message, String chatRoomId) {
        this.message = message;
        this.chatRoomId = chatRoomId;
    }

}