package com.example.week7project.controller;


import com.example.week7project.dto.TownCommentDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.service.TownCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class TownController {

    private final TownCommentService townCommentService;

    //댓글 쓰기
    @PostMapping("/api/towncomment/{id}")
    public ResponseDto<?> writeComment(@PathVariable Long postId, @RequestBody TownCommentDto townCommentDto, HttpServletRequest request) {
        return townCommentService.writeComment(postId, townCommentDto, request);
    }

    //댓글 목록 가져오기
    @GetMapping("/api/towncomment/{id}")
    public ResponseDto<?> readAllComments(@PathVariable Long postId,  HttpServletRequest request) {
        return townCommentService.readAllComments(postId, request);
    }


}
