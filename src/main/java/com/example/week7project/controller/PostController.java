package com.example.week7project.controller;

import com.example.week7project.dto.request.PostRequestDto;
import com.example.week7project.dto.request.StatusRequestDto;
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

    // 연관상품 검색
    @RequestMapping (value = "/api/view/categorylist/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getCategoryList(@PathVariable Long id) {
        return postService.getCategoryList(id);
    }


    // 판매자등록 상품 검색
    @RequestMapping (value = "/api/view/sellerproduct/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getSellerProduct(@PathVariable Long id) {
        return postService.getProductList(id);
    }


    // 판매글 상태변경 (판매중, 예약중)
    @RequestMapping (value = "/api/post/status/{id}", method = RequestMethod.PUT)
    public ResponseDto<?> switchStatus(@PathVariable Long id, @RequestBody StatusRequestDto statusRequestDto, HttpServletRequest request) {
        return postService.switchStatus(id, statusRequestDto, request);
    }

    // 판매글 상태변경 (거래, 나눔완료)
    @RequestMapping (value = "/api/post/status/done/{id}", method = RequestMethod.PUT)
    public ResponseDto<?> switchStatusDone(@PathVariable Long id, @RequestBody StatusRequestDto statusRequestDto, HttpServletRequest request) {
        return postService.switchStatusDone(id, statusRequestDto, request);
    }

    // 판매글 채팅방 목록 불러오기 (id와 구매자만)
    @RequestMapping (value = "/api/post/getchatlist/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getChatList(@PathVariable Long id, HttpServletRequest request) {
        return postService.getPostChatRoom(id, request);
    }
}