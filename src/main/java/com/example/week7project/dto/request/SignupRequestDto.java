package com.example.week7project.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 회원가입 요청 dto
public class SignupRequestDto {
    private String phoneNumber;
    private String nickname;
    private String password;
}
