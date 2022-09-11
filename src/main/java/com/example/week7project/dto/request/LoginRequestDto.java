package com.example.week7project.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 로그인 요청 dto
public class LoginRequestDto {
    private String phoneNumber;
    private String password;
}
