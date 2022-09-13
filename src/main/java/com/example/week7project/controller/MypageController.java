package com.example.week7project.controller;


import com.example.week7project.dto.request.UpdateProfileDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class MypageController {

    private final MyPageService myPageService;

    // 닉네임 수정
    @PutMapping("/api/user/nickname")
    public ResponseDto<?> updateNickname(@RequestBody UpdateProfileDto updateProfileDto,
                                         HttpServletRequest request, HttpServletResponse response) {
        return myPageService.updateProfile("nickname", updateProfileDto, request, response);
    }

    // 주소 수정
    @PutMapping("/api/user/address")
    public ResponseDto<?> updateAddress(@RequestBody UpdateProfileDto updateProfileDto,
                                        HttpServletRequest request, HttpServletResponse response) {
        return myPageService.updateProfile("address", updateProfileDto, request, response);
    }

    // 판매글 목록조회
    @GetMapping("/api/user/salespost")
    public ResponseDto<?> getSellPost(HttpServletRequest request) {
        return myPageService.getSellPost(request);
    }

    // 구매글 목록조회
    @GetMapping("/api/user/purchasepost")
    public ResponseDto<?> getPurchasePost(HttpServletRequest request) {
        return myPageService.getPurchasePost(request);
    }

    // 관심상품 목록조회
    @GetMapping("/api/user/wishlist")
    public ResponseDto<?> getWishPost(HttpServletRequest request) {
        return myPageService.getWishPost(request);
    }

    // 회원 정보 조회
    @GetMapping("/api/user")
    public ResponseDto<?> getMemberProfile(HttpServletRequest request) {
        return myPageService.getMemberProfile(request);
    }

    // 회원 채팅방 조회
    @GetMapping("/api/chat")
    public ResponseDto<?> getChatRooms(HttpServletRequest request) {
        return myPageService.getChatRooms(request);
    }
}
