package com.example.week7project.controller;

import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.service.WishService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    //관심상품 누르기
    @PostMapping("/api/addwhishlist/{id}")
    public ResponseDto<?> addWhishPost(@PathVariable Long id, HttpServletRequest request) {
        return wishService.addWishPost(id, request);
    }

    // 관심상품 누르기 취소
    @PostMapping("/api/removewhishlist/{id}")
    public ResponseDto<?> removeWhishPost(@PathVariable Long id, HttpServletRequest request) {
        return wishService.removeWishPost(id, request);
    }

    // 관심상품 확인
    @GetMapping("/api/getwhishlist/{id}")
    public ResponseDto<?> checkStatus(@PathVariable Long id, HttpServletRequest request) {
        return wishService.checkStatus(id, request);
    }
}
