package com.example.week7project.controller;

import com.example.week7project.dto.request.PostRequestDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    //게시글 전체 조회
    @GetMapping("/api/view/post")
    public ResponseDto<?> readAllPosts() {
        return postService.readAllPosts();
    }

    //게시글 조회.
    @GetMapping("/api/view/post/{id}")
    public ResponseDto<?> readPost(@PathVariable Long id) {
        return postService.readPost(id);
    }

    //게시글 작성
    @PostMapping("/api/post")
    public ResponseDto<?> writePost(@RequestBody PostRequestDto postRequestDto, HttpServletRequest request) {
        return postService.writePost(postRequestDto, request);
    }

    //게시글 수정
    @PutMapping("/api/post/{id}")
    public ResponseDto<?> modifyPost(@PathVariable Long id, @RequestBody PostRequestDto requestDto, HttpServletRequest request) {
        return postService.updatePost(id, requestDto, request);
    }

    //게시글 삭제
    @DeleteMapping("/api/post/{id}")
    public ResponseDto<?> deletePost(@PathVariable Long id, HttpServletRequest request) {
        return postService.deletePost(id, request);
    }
}