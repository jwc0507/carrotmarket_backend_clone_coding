package com.example.week7project.dto;

import com.example.week7project.domain.enums.MessageType;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ChatMessageDto {
    private MessageType messageType;
    private Long roomId;
    private String sender;
    private String message;
}