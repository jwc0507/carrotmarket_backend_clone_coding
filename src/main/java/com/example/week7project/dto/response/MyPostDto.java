package com.example.week7project.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyPostDto {
    private Long id;
    private String title;
    private String imgUrl;
    private Long price;
}
