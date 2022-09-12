package com.example.week7project.service;

import com.example.week7project.domain.Member;
import com.example.week7project.domain.Post;
import com.example.week7project.domain.WishList;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.repository.PostRepository;
import com.example.week7project.repository.WishListRepository;
import com.example.week7project.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WishService {

    private final TokenProvider tokenProvider;
    private final PostRepository postRepository;
    private final WishListRepository wishListRepository;



    // 관심 상품 등록
    @Transactional
    public ResponseDto<?> addWishPost(Long id, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = (Member) chkResponse.getData();
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty())
            return ResponseDto.fail("해당 상품이 존재하지 않습니다.");
        if (wishListRepository.findByMemberIdAndPostId(member.getId(), post.get().getId()) != null) {
            return ResponseDto.fail("이미 해당 상품이 관심목록에 존재합니다.");
        }

        WishList wishList = WishList.builder()
                .member(member)
                .post(post.get())
                .build();

        wishListRepository.save(wishList);
        return ResponseDto.success("관심상품 추가가 완료되었습니다.");
    }

    @Transactional
    public ResponseDto<?> removeWishPost(Long id, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = (Member) chkResponse.getData();
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty())
            return ResponseDto.fail("해당 상품이 존재하지 않습니다.");

        WishList wishList = wishListRepository.findByMemberIdAndPostId(member.getId(), post.get().getId());
        if (wishList == null) {
            return ResponseDto.fail("아직 관심상품에 등록되지 않았습니다.");
        }
        wishListRepository.delete(wishList);
        return ResponseDto.success("관심상품 취소가 완료되었습니다.");
    }

    public ResponseDto<?> checkStatus(Long id, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = (Member) chkResponse.getData();
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty())
            return ResponseDto.fail("해당 상품이 존재하지 않습니다.");
        if (wishListRepository.findByMemberIdAndPostId(member.getId(), post.get().getId()) != null) {
            return ResponseDto.success(true);
        } else {
            return ResponseDto.success(false);
        }
    }


    // 글 id로 글찾기
    @Transactional
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    // 토큰체크
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
