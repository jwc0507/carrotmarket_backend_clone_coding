package com.example.week7project.controller;

import com.example.week7project.dto.request.ChatRoomRequestDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    // 채팅방 생성
    @RequestMapping (value = "/api/chat/{id}", method = RequestMethod.POST)
    public ResponseDto<?> createChatRoom(@PathVariable Long id, @RequestBody ChatRoomRequestDto requestDto, HttpServletRequest request) {
        return chatService.createRoom(id, requestDto, request);
    }

    // 게시글 채팅방 조회
    @RequestMapping (value = "/api/chat/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getChatRoomId(@PathVariable Long id, HttpServletRequest request) {
        return chatService.getRoomId(id, request);
    }

    // 회원 채팅방 조회
    @GetMapping("/api/chat")
    public ResponseDto<?> getChatRooms(HttpServletRequest request) {
        return chatService.getChatRooms(request);
    }
}
