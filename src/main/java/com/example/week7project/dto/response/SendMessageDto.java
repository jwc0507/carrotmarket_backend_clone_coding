package com.example.week7project.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SendMessageDto {
    private Long roomId;
    private Long postId;
    private String sender;
    private String message;
}
