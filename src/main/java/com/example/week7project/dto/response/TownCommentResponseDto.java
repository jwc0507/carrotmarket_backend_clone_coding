package com.example.week7project.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TownCommentResponseDto {
    private Long id;
    private Long townPostId;
    private String nickname;
    private String address;
    private String content;
    private String time;
}
