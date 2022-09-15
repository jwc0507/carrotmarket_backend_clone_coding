package com.example.week7project.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LastChatDto {
    private String time;
    private String message;
}
