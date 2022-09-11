package com.example.week7project.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MyAllPostDto {
    List<MyPostDto> postList;
}
