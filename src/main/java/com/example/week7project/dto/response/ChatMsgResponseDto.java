package com.example.week7project.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ChatMsgResponseDto {
    private String type;
    private String nickname;
    private String message;
}
