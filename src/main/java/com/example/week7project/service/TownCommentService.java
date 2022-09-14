package com.example.week7project.service;

import com.example.week7project.domain.Member;
import com.example.week7project.domain.TownComment;
import com.example.week7project.domain.TownPost;
import com.example.week7project.dto.TownCommentDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.dto.response.TownCommentResponseDto;
import com.example.week7project.repository.TownCommentRepository;
import com.example.week7project.repository.TownPostRepository;
import com.example.week7project.security.TokenProvider;
import com.example.week7project.time.Time;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TownCommentService {

    private final TownPostRepository townPostRepository;
    private final TownCommentRepository townCommentRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> writeComment(Long postId, TownCommentDto chatCommentDto, HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = (Member) chkResponse.getData();

        Optional<TownPost> getTownPost = townPostRepository.findById(postId);
        TownPost townPost;
        if (getTownPost.isPresent())
            townPost = getTownPost.get();
        else
            return ResponseDto.fail("게시글이 존재하지 않습니다.");

        TownComment townComment = TownComment
                .builder()
                .content(chatCommentDto.getContent())
                .member(member)
                .townPost(townPost)
                .build();

        townCommentRepository.save(townComment);

        townPost.addCommentCount(1);

        return ResponseDto.success(
                TownCommentResponseDto.builder()
                        .id(townComment.getId())
                        .townPostId(townComment.getTownPost().getId())
                        .nickname(townComment.getMember().getNickname())
                        .address(townComment.getMember().getAddress())
                        .content(townComment.getContent())
                        .time(Time.convertLocaldatetimeToTime(townComment.getCreatedAt()))
                        .build()
        );
    }

    public ResponseDto<?> readAllComments(Long postId, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        Optional<TownPost> getTownPost = townPostRepository.findById(postId);
        TownPost townPost;
        if (getTownPost.isPresent())
            townPost = getTownPost.get();
        else
            return ResponseDto.fail("게시글이 존재하지 않습니다.");

        List<TownComment> townComments = townCommentRepository.findByTownPost(townPost);
        List<TownCommentResponseDto> townCommentResponseDtoList = new ArrayList<>();

        for (TownComment townComment : townComments) {
            townCommentResponseDtoList.add(
                    TownCommentResponseDto.builder()
                            .id(townComment.getId())
                            .townPostId(townComment.getTownPost().getId())
                            .nickname(townComment.getMember().getNickname())
                            .address(townComment.getMember().getAddress())
                            .content(townComment.getContent())
                            .time(Time.convertLocaldatetimeToTime(townComment.getCreatedAt()))
                            .build()
            );
        }
        return ResponseDto.success(townCommentResponseDtoList);
    }

    private ResponseDto<?> validateCheck(HttpServletRequest request) {
        if (null == request.getHeader("RefreshToken") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }
        return ResponseDto.success(member);
    }

    // refreshtoken으로 유저찾기
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
