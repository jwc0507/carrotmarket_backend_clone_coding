package com.example.week7project.controller;

import com.example.week7project.dto.request.DuplicationRequestDto;
import com.example.week7project.dto.request.LoginRequestDto;
import com.example.week7project.dto.request.SignupRequestDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입
    @RequestMapping(value = "/api/member/signup", method = RequestMethod.POST)
    public ResponseDto<?> signup(@RequestBody SignupRequestDto requestDto) {
        return memberService.createMember(requestDto);
    }

    // 로그인
    @RequestMapping(value = "/api/member/login", method = RequestMethod.POST)
    public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response){
        return memberService.login(requestDto, response);
    }

    // 로그아웃
    @RequestMapping(value = "/api/member/logout", method = RequestMethod.POST)
    public ResponseDto<?> logout(HttpServletRequest request) {
        return memberService.logout(request);
    }

    // 전화번호 중복 확인
    @RequestMapping(value = "/api/member/chkPhonenum", method = RequestMethod.POST)
    public ResponseDto<?> checkDuplicationPhoneNumber(@RequestBody DuplicationRequestDto requestDto) {
        return memberService.checkPhoneNumber(requestDto);
    }

    // 닉네임 중복 확인
    @RequestMapping(value = "/api/member/chkNickname", method = RequestMethod.POST)
    public ResponseDto<?> checkDuplicationNickname(@RequestBody DuplicationRequestDto requestDto) {
        return memberService.checkNickname(requestDto);
    }
}
