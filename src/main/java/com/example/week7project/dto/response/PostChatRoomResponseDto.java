package com.example.week7project.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class PostChatRoomResponseDto {
    private Long roomId;
    private String roomName;
    private String buyerNickname;
    private String address;
    private String time;
}
