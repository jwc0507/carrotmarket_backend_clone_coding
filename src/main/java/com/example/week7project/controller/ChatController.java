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

    // 채팅방 조회
}
