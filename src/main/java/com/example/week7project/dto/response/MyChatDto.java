package com.example.week7project.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyChatDto {
    private Long id;
    private Long senderId;
    private String address;
    private String message;
    private String lastTime;
}
