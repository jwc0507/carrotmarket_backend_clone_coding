package com.example.week7project.service;

import com.example.week7project.domain.Member;
import com.example.week7project.domain.TownPost;
import com.example.week7project.dto.request.TownPostRequestDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.dto.response.TownPostListResponseDto;
import com.example.week7project.dto.response.TownPostResponseDto;
import com.example.week7project.repository.MemberRepository;
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
public class TownPostService {
    private final MemberRepository memberRepository;
    private final TownPostRepository townPostRepository;
    private final TokenProvider tokenProvider;

    // 게시글 생성
    @Transactional
    public ResponseDto<?> createTownPost(TownPostRequestDto requestDto, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = (Member) chkResponse.getData();
        // 유저 테이블에서 유저객체 가져오기
        Member updateMember = memberRepository.findByNickname(member.getNickname()).get();

        String content = requestDto.getContent();
        String imageUrl = requestDto.getImageUrl();
        String nickname = updateMember.getNickname();
        String address = updateMember.getAddress();

        TownPost townPost = TownPost.builder()
                .content(content)
                .imgUrl(imageUrl)
                .numOfComment(0)
                .numOfWatch(0)
                .member(updateMember)
                .build();

        townPostRepository.save(townPost);

        return ResponseDto.success(TownPostListResponseDto.builder()
                .id(townPost.getId())
                .content(townPost.getContent())
                .nickname(nickname)
                .address(address)
                .imgUrl(townPost.getImgUrl())
                .time(Time.convertLocaldatetimeToTime(townPost.getCreatedAt()))
                .numOfComment(townPost.getNumOfComment())
                .build());
    }

    // 게시글 전체 보기
    public ResponseDto<?> getTownPostList(HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        List<TownPost> list = townPostRepository.findByOrderByCreatedAtDesc();
        List<TownPostListResponseDto> townPostListResponseDtoList = new ArrayList<>();

        for (TownPost townPost : list) {
            Member getMember = townPost.getMember();
            TownPostListResponseDto townPostListResponseDto = TownPostListResponseDto.builder()
                    .id(townPost.getId())
                    .content(townPost.getContent())
                    .nickname(getMember.getNickname())
                    .address(getMember.getAddress())
                    .imgUrl(townPost.getImgUrl())
                    .time(Time.convertLocaldatetimeToTime(townPost.getCreatedAt()))
                    .numOfComment(townPost.getNumOfComment())
                    .build();
            townPostListResponseDtoList.add(townPostListResponseDto);
        }

        return ResponseDto.success(townPostListResponseDtoList);
    }

    @Transactional
    // 게시글 상세보기
    public ResponseDto<?> getTownPost(Long townPostId, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        Optional<TownPost> getTownPost = townPostRepository.findById(townPostId);
        TownPost townPost;
        if(getTownPost.isPresent())
            townPost = getTownPost.get();
        else
            return ResponseDto.fail("게시글이 없습니다.");

        Member getMember = townPost.getMember();
        TownPostResponseDto townPostResponseDto = TownPostResponseDto.builder()
                .id(townPostId)
                .content(townPost.getContent())
                .nickname(getMember.getNickname())
                .address(getMember.getAddress())
                .imgUrl(townPost.getImgUrl())
                .time(Time.convertLocaldatetimeToTime(townPost.getCreatedAt()))
                .numOfWatch(townPost.getNumOfWatch())
                .numOfComment(townPost.getNumOfComment())
                .build();

        townPost.addWatchCount();

        return ResponseDto.success(townPostResponseDto);
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

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
