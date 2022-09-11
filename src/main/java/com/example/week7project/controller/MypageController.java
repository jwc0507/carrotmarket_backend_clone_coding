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

@RestController
@RequiredArgsConstructor
public class MypageController {

    private final MyPageService myPageService;

    // 회원 정보 수정
    @PutMapping("/api/user")
    public ResponseDto<?> updateProfile(@RequestBody UpdateProfileDto updateProfileDto,
                                        HttpServletRequest request) {
        return myPageService.updateProfile(updateProfileDto, request);
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

}
