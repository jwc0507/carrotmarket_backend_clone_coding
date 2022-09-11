package com.example.week7project.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private double temperature;
    private String title;
    private String status;
    private String category;
    private String nickname;
    private String address;
    private String imgUrl;
    private long price;
    private String content;
    private int numOfChat;
    private int numOfWish;
}
