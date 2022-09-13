package com.example.week7project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimePostResponseDto {
    private Long id;
    private double temperature;
    private String title;
    private String status;
    private String category;
    private String nickname;
    private String address;
    private String imgUrl;
    private String time;
    private long price;
    private String content;
    private int numOfChat;
    private int numOfWish;
}
