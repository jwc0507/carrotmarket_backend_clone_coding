package com.example.week7project.dto.request;


import com.example.week7project.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PostRequestDto {

    //attribute 선언할 때 final 붙여야 하나?
    private final String title;
    private final String status;
    private final String category;
    private final String imageUrl;
    private final long price;
    private final String content;
    private final int numOfChat;
    private final int numOfWish;
    private final Member member; //foreign key도 Dto에 필드 생성 해줘야하는가?


}
