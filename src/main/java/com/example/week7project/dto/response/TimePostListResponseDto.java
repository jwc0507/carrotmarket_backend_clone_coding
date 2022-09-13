package com.example.week7project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimePostListResponseDto {

    private Long id;
    private String title;
    private String imgUrl;
    private String time;
    private String status;
    private String address;
    private long price;
    private int numOfChat;
    private int numOfWish;
}
