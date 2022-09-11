package com.example.week7project.service;

import com.example.week7project.domain.Member;
import com.example.week7project.domain.Post;
import com.example.week7project.domain.PurchaseList;
import com.example.week7project.domain.WishList;
import com.example.week7project.dto.request.UpdateProfileDto;
import com.example.week7project.dto.response.MyPostDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.repository.MemberRepository;
import com.example.week7project.repository.PostRepository;
import com.example.week7project.repository.PurchaseListRepository;
import com.example.week7project.repository.WishListRepository;
import com.example.week7project.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private TokenProvider tokenProvider;
    private MemberRepository memberRepository;
    private PostRepository postRepository;
    private PurchaseListRepository purchaseListRepository;
    private WishListRepository wishListRepository;

    /**
     * 멤버 프로필 수정
     */
    public ResponseDto<?> updateProfile(UpdateProfileDto updateProfileDto, HttpServletRequest request) {

        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);

        if(!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();

        // 객체 DB에서 가져오기.
        member.builder()
                .phoneNumber(member.getPhoneNumber())
                .nickname(updateProfileDto.getNickName())
                .address(updateProfileDto.getAddress())
                .temperature(member.getTemperature());
        memberRepository.save(member);
        return ResponseDto.success(member.getNickname() + "수정완료");
    }

    /**
     * 판매글 목록 조회
     */
    public ResponseDto<?> getSellPost(HttpServletRequest request) {

        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);

        if(!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();

        List<Post> sellList = postRepository.findByMemberId(member.getId());
        if (sellList.isEmpty())
            return ResponseDto.fail("판매한 내역이 없습니다.");

        List<MyPostDto> myPostDtoList = new ArrayList<>();
        for (Post post : sellList) {
            myPostDtoList.add(
                    MyPostDto.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .imgUrl(post.getImageUrl())
                            .price(post.getPrice())
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

        if(!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();

        List<PurchaseList> purchaseList = purchaseListRepository.findByMemberId(member.getId());
        if (purchaseList.isEmpty())
            return ResponseDto.fail("구매한 내역이 없습니다.");

        List<MyPostDto> myPostDtoList = new ArrayList<>();
        for (PurchaseList list : purchaseList) {
            myPostDtoList.add(
                    MyPostDto.builder()
                            .id(list.getPost().getId())
                            .title(list.getPost().getTitle())
                            .imgUrl(list.getPost().getImageUrl())
                            .price(list.getPost().getPrice())
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

        if(!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();

        List<WishList> wishList = wishListRepository.findByMemberId(member.getId());
        if (wishList.isEmpty())
            return ResponseDto.fail("관심상품이 없습니다.");

        List<MyPostDto> myPostDtoList = new ArrayList<>();
        for (WishList list : wishList) {
            myPostDtoList.add(
                    MyPostDto.builder()
                            .id(list.getId())
                            .title(list.getPost().getTitle())
                            .imgUrl(list.getPost().getImageUrl())
                            .price(list.getPost().getPrice())
                            .build()
            );
        }
        return ResponseDto.success(myPostDtoList);
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
