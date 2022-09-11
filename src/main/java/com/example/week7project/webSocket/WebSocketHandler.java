package com.example.week7project.webSocket;

import com.example.week7project.domain.ChatRoom;
import com.example.week7project.dto.ChatMessageDto;
import com.example.week7project.dto.ChatRoomDto;
import com.example.week7project.repository.ChatRoomRepository;
import com.example.week7project.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("{}", payload);
        ChatMessageDto chatMessagedto = objectMapper.readValue(payload, ChatMessageDto.class);

        Optional<?> getChatRoom = chatRoomRepository.findById(chatMessagedto.getRoomId());
        ChatRoom chatRoom;
        if (getChatRoom.isPresent()) {
            chatRoom = (ChatRoom) getChatRoom.get();
            ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                    .roomId(chatRoom.getId())
                    .name(chatRoom.getName())
                    .build();
            chatRoomDto.handlerActions(session, chatMessagedto, chatService);
        }
    }
}