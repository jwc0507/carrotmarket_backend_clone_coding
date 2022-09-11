package com.example.week7project.dto.request;


import com.example.week7project.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PostRequestDto {
    private final String title;
    private final long price;
    private final String imageUrl;
    private final String category;
    private final String content;
}
