package com.example.week7project.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyCategoryPostDto {
    private Long id;
    private String title;
    private String imgUrl;
    private String status;
    private Long price;
}
