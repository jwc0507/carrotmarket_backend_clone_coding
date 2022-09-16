package com.example.week7project.service;


import com.example.week7project.domain.*;
import com.example.week7project.dto.TokenDto;
import com.example.week7project.dto.request.UpdateProfileDto;
import com.example.week7project.dto.response.MemberProfileDto;
import com.example.week7project.dto.response.MyPostDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.repository.*;
import com.example.week7project.security.TokenProvider;
import com.example.week7project.time.Time;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;


@Service
@RequiredArgsConstructor
public class MyPageService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PurchaseListRepository purchaseListRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final WishListRepository wishListRepository;

    /**
     * 멤버 프로필 수정
     */
    @Transactional
    public ResponseDto<?> updateProfile(String type, UpdateProfileDto updateProfileDto, HttpServletRequest request, HttpServletResponse response) {

        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);

        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = (Member) chkResponse.getData();

        Member updateMember = memberRepository.findById(member.getId()).get();

        // Member 객체 업데이트
        if (type.equals("nickname"))
            updateMember.updateNickname(updateProfileDto);
        else if (type.equals("address"))
            updateMember.updateAddress(updateProfileDto);

        refreshTokenRepository.delete(refreshTokenRepository.findByMember(updateMember).get());

        // 토큰 재생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(updateMember);

        //헤더에 반환 to FE
        response.addHeader("Authorization","Bearer "+tokenDto.getAccessToken());
        response.addHeader("RefreshToken", tokenDto.getRefreshToken());

        return ResponseDto.success(" 수정완료");
    }

    /**
     * 판매글 목록 조회
     */
    public ResponseDto<?> getSellPost(HttpServletRequest request) {

        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);

        if (!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();

        List<Post> sellList = postRepository.findByMemberId(member.getId());
        List<MyPostDto> myPostDtoList = new ArrayList<>();
        if (sellList.isEmpty())
            return ResponseDto.success(myPostDtoList);

        for (Post post : sellList) {
            myPostDtoList.add(
                    MyPostDto.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .imgUrl(post.getImageUrl())
                            .status(post.getStatus())
                            .price(post.getPrice())
                            .address(post.getMember().getAddress())
                            .time(Time.convertLocaldatetimeToTime(post.getCreatedAt()))
                            .numOfChatroom(post.getNumOfChat())
                            .numOfWish(post.getNumOfWish())
                            .build());
        }
        return ResponseDto.success(myPostDtoList);
    }

    /**
     * 구매글 목록 조회
     */
    public ResponseDto<?> getPurchasePost(HttpServletRequest request) {

        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);

        if (!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();

        List<PurchaseList> purchaseList = purchaseListRepository.findByMemberId(member.getId());
        List<MyPostDto> myPostDtoList = new ArrayList<>();
        if (purchaseList.isEmpty())
            return ResponseDto.success(myPostDtoList);

        for (PurchaseList list : purchaseList) {
            myPostDtoList.add(
                    MyPostDto.builder()
                            .id(list.getPost().getId())
                            .title(list.getPost().getTitle())
                            .imgUrl(list.getPost().getImageUrl())
                            .status(list.getPost().getStatus())
                            .price(list.getPost().getPrice())
                            .address(list.getPost().getMember().getAddress())
                            .time(Time.convertLocaldatetimeToTime(list.getPost().getCreatedAt()))
                            .numOfChatroom(list.getPost().getNumOfChat())
                            .numOfWish(list.getPost().getNumOfWish())
                            .build()
            );
        }
        return ResponseDto.success(myPostDtoList);
    }

    /**
     * 관심상품 목록조회
     */
    public ResponseDto<?> getWishPost(HttpServletRequest request) {
        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);

        if (!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();

        List<WishList> wishList = wishListRepository.findByMemberId(member.getId());
        List<MyPostDto> myPostDtoList = new ArrayList<>();
        if (wishList.isEmpty())
            return ResponseDto.success(myPostDtoList);

        for (WishList list : wishList) {
            myPostDtoList.add(
                    MyPostDto.builder()
                            .id(list.getPost().getId())
                            .title(list.getPost().getTitle())
                            .imgUrl(list.getPost().getImageUrl())
                            .status(list.getPost().getStatus())
                            .price(list.getPost().getPrice())
                            .address(list.getPost().getMember().getAddress())
                            .time(Time.convertLocaldatetimeToTime(list.getPost().getCreatedAt()))
                            .numOfChatroom(list.getPost().getNumOfChat())
                            .numOfWish(list.getPost().getNumOfWish())
                            .build()
            );
        }
        return ResponseDto.success(myPostDtoList);
    }

    /**
     * 멤버 정보 가져오기
     */
    public ResponseDto<?> getMemberProfile(HttpServletRequest request) {
        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);

        if (!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();
        Optional<Member> repositoryMember = memberRepository.findByPhoneNumber(member.getPhoneNumber());

        List<Post> sellPost = postRepository.findByMemberId(repositoryMember.get().getId());
        int numOfSale = 0;
        for (Post post : sellPost) {
            // == status 관련 코드 합친 후 재수정 예정 ==//
            if (post.getStatus().equals("판매중")) {
                numOfSale++;
            }
        }

        return ResponseDto.success(
                MemberProfileDto.builder()
                        .nickname(repositoryMember.get().getNickname())
                        .id(repositoryMember.get().getId())
                        .address(repositoryMember.get().getAddress())
                        .temperature(repositoryMember.get().getTemperature())
                        .numOfSale(numOfSale)
                        .build()
        );
    }




    // RefreshToken 유효성 검사
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

    private ResponseDto<?> validateCheck(HttpServletRequest request) {

        // RefreshToken 및 Authorization 유효성 검사
        if (null == request.getHeader("RefreshToken") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");

        }

        Member member = validateMember(request);

        // token 정보 유효성 검사
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");

        }
        return ResponseDto.success(member);
    }


}
