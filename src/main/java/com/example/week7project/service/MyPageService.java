package com.example.week7project.service;

import com.example.week7project.domain.*;
import com.example.week7project.dto.request.UpdateProfileDto;
import com.example.week7project.dto.response.MemberProfileDto;
import com.example.week7project.dto.response.MyChatDto;
import com.example.week7project.dto.response.MyPostDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.repository.*;
import com.example.week7project.security.TokenProvider;
import com.example.week7project.time.Time;
import com.sun.source.tree.TryTree;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PurchaseListRepository purchaseListRepository;
    private final WishListRepository wishListRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * 멤버 프로필 수정
     */
    @Transactional
    public ResponseDto<?> updateProfile(UpdateProfileDto updateProfileDto, HttpServletRequest request) {

        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);

        if (!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();

        Optional<Member> findMember = memberRepository.findByPhoneNumber(member.getPhoneNumber());

        if (findMember.isEmpty())
            return ResponseDto.fail("회원이 존재하지 않습니다.");

        // Member 객체 업데이트
        findMember.get().updateMember(updateProfileDto);
        return ResponseDto.success(findMember.get().getNickname() + " 수정완료");
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

        if (!chkResponse.isSuccess())
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

        if (!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();

        List<WishList> wishList = wishListRepository.findByMemberId(member.getId());
        if (wishList.isEmpty())
            return ResponseDto.fail("관심상품이 없습니다.");

        List<MyPostDto> myPostDtoList = new ArrayList<>();
        for (WishList list : wishList) {
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

    /**
     * 회원 채팅방 조회
     */
    public ResponseDto<?> getChatRooms(HttpServletRequest request) {
        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);

        if (!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();
        List<MyChatDto> chatDtoList = new ArrayList<>();            // 채팅방 리스트 담을 용도
        // 내가 쓴 글 기준으로 찾아오기
        List<Post> postList = postRepository.findByMemberId(member.getId());
        for (Post post : postList) {
            System.out.println("post = " + post);
            if (chatRoomRepository.findByPost(post) == null) {
                continue;
            } else {
                ChatRoom chatRoom = chatRoomRepository.findByPost(post);
                System.out.println("chatRoom = " + chatRoom.getId());
                Long chatRoomId = chatRoom.getId();
                Member buyer = chatRoom.getMember();
                Long buyerId = buyer.getId();
                String address = buyer.getAddress();
                String message;
                LocalDateTime lastTime;
                // == 채팅 내역 가져와야 함.
                if (chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom).isEmpty()) {
                    message = " ";
                    lastTime = LocalDateTime.MIN;
                } else {
                    List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom);
                    message = chatMessageList.get(0).getMessage();
                    lastTime = chatMessageList.get(0).getCreatedAt();
                }
                chatDtoList.add(
                        MyChatDto.builder()
                                .id(chatRoomId)
                                .senderId(buyerId)
                                .address(address)
                                .message(message)
                                .lastTime(Time.convertLocaldatetimeToTime(lastTime))
                                .build()
                );
            }
        }

        // 내가 구매한 기준으로 찾아오기
        List<ChatRoom> chatRooms = chatRoomRepository.findByMember(member);
        for (ChatRoom chatRoom : chatRooms) {
            Long chatRoomId = chatRoom.getId();
            System.out.println("chatRoomId = " + chatRoomId);
            Member seller = chatRoom.getPost().getMember();
            Long sellerId = seller.getId();
            String address = seller.getAddress();
            String message;
            LocalDateTime lastTime;
            //== 채팅 내역 가져와야 함.
            if (chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom).isEmpty()) {
                message = " ";
                lastTime = LocalDateTime.MIN;
            } else {
                List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom);
                message = chatMessageList.get(0).getMessage();
                lastTime = chatMessageList.get(0).getCreatedAt();
            }
            chatDtoList.add(
                    MyChatDto.builder()
                            .id(chatRoomId)
                            .senderId(sellerId)
                            .address(address)
                            .message(message)
                            .lastTime(Time.convertLocaldatetimeToTime(lastTime))
                            .build()
            );
        }

        return ResponseDto.success(chatDtoList);
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
