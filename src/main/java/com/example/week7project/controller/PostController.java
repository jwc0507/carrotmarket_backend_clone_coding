package com.example.week7project.controller;

import com.example.week7project.dto.ImageDeleteResponseDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 이미지업로드
    @RequestMapping(value = "/api/post/image", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseDto<?> uploadImage(@RequestPart("image") MultipartFile file, HttpServletRequest request) {
        return postService.uploadImage(file, request);
    }

    // 이미지변경
    @RequestMapping(value = "/api/post/image", method = RequestMethod.PUT, consumes = {"multipart/form-data"})
    public ResponseDto<?> updateImage(@RequestPart("image") MultipartFile file, HttpServletRequest request) {
        return postService.updateImage(file, request);
    }

    // 이미지삭제
    @RequestMapping(value = "/api/post/image", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteImage(@RequestBody ImageDeleteResponseDto responseDto, HttpServletRequest request) {
        return postService.deleteImage(responseDto, request);
    }
}
