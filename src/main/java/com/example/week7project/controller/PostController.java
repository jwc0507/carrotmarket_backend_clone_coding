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

    private final PostRepository postRepository;
    private final PostService postService;

    //게시글 전체 조회
    @GetMapping("api/view/post")
    public ResponseDto<List<Post>> getPosts() {
        List<Post> postList = postRepository.findAll();
        return ResponseDto.success(postList);
    }
    //게시글 조회.

    @GetMapping("api/view/post/{id}")
    public ResponseDto<Post> getPost(@PathVariable Long id) {
        //Optional<Post> post = postRepository.findById(id);
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );
        return ResponseDto.success(post);
    }

    //게시글 작성
    @PostMapping("/api/post")
    public ResponseDto<String> writePost(@RequestBody PostRequestDto postRequestDto) throws Exception {
        Post post = new Post(postRequestDto);
        return ResponseDto.success("글 작성 완료");

    }

    //게시글 수정
    @PutMapping("api/post/{id}")
    public ResponseDto<String> modifyPost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        return postService.update(id, requestDto);
    }

    @DeleteMapping("api/post/{id}")
    public ResponseDto<String> deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
        return ResponseDto.success("글 삭제 완료");
    }
}