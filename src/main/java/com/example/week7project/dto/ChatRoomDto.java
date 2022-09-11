package com.example.week7project.dto;

import com.example.week7project.domain.enums.MessageType;
import com.example.week7project.service.ChatService;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ChatRoomDto {
    private Long roomId;
    private String name;
    private Set<WebSocketSession> sessions = new HashSet<>();

    @Builder
    public ChatRoomDto(Long roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }

    public void handlerActions(WebSocketSession session, ChatMessageDto chatMessageDto, ChatService chatService) {
        if (chatMessageDto.getMessageType().equals(MessageType.ENTER)) {
            sessions.add(session);
            chatMessageDto.setMessage(chatMessageDto.getSender() + "님이 입장했습니다.");
        }
        sendMessage(chatMessageDto, chatService);
    }

    private <T> void sendMessage(T message, ChatService chatService) {
        sessions.parallelStream()
                .forEach(session -> chatService.sendMessage(session, message));
    }
}
