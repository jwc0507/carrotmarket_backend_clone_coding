package com.example.week7project.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TownPostListResponseDto {
    private Long id;
    private String content;
    private String nickname;
    private String address;
    private String imgUrl;
    private String time;
    private int numOfComment;
}
