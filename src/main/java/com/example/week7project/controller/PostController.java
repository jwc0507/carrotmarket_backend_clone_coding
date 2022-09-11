package com.example.week7project.controller;

import com.example.week7project.domain.Post;
import com.example.week7project.dto.request.PostRequestDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.repository.PostRepository;
import com.example.week7project.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    //게시글 전체 조회
    @GetMapping("api/view/post")
    public ResponseDto<List<Post>> readAllPosts() {
        return postService.readAllPosts();
    }
    //게시글 조회.
    @GetMapping("api/view/post/{id}")
    public ResponseDto<Post> readPost(@PathVariable Long id) {

        return postService.readPost(id);
    }
    //게시글 작성
    @PostMapping("/api/post")
    public ResponseDto<String> writePost(@RequestBody PostRequestDto postRequestDto) throws Exception {
        return postService.writePost(postRequestDto);
    }
    //게시글 수정
    @PutMapping("api/post/{id}")
    public ResponseDto<String> modifyPost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        return postService.updatePost(id, requestDto);
    }
    //게시글 삭제
    @DeleteMapping("api/post/{id}")
    public ResponseDto<String> deletePost(@PathVariable Long id) {
        return postService.deletePost(id);
    }
}