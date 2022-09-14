package com.example.week7project.controller;

import com.example.week7project.dto.request.TownPostRequestDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.service.TownPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class TownPostController {

    private final TownPostService townPostService;

    // 동네 게시글 작성
    @RequestMapping (value = "/api/townpost", method = RequestMethod.POST)
    public ResponseDto<?> createTownPost(@RequestBody TownPostRequestDto requestDto, HttpServletRequest request) {
        return townPostService.createTownPost(requestDto, request);
    }

    // 동네 게시글 리스트(전체) 보기
    @RequestMapping (value = "/api/townpost", method = RequestMethod.GET)
    public ResponseDto<?> getTownPostList(HttpServletRequest request) {
        return townPostService.getTownPostList(request);
    }

    // 동네 게시글 상세보기
    @RequestMapping (value = "/api/townpost/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getTownPost(@PathVariable Long id, HttpServletRequest request) {
        return townPostService.getTownPost(id, request);
    }
}
