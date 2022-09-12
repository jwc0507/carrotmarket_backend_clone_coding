package com.example.week7project.dto.response;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberProfileDto {

    private String nickname;
    private Long id;
    private double temperature;
    private int numOfSale;
}
