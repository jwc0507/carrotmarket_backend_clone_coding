package com.example.week7project.dto.request;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ChatMessageDto {
    private Long roomId;
    private String sender;
    private String message;
}