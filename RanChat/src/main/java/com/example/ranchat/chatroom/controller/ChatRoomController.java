package com.example.ranchat.chatroom.controller;

import com.example.ranchat.chatroom.dto.MatchingResponseDTO;
import com.example.ranchat.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    @PostMapping("/ranchat")
    public ResponseEntity<MatchingResponseDTO> ranChat(@RequestParam String userId) {
        MatchingResponseDTO response = chatRoomService.ranChat(userId);
        return ResponseEntity.ok(response);
    }
}
