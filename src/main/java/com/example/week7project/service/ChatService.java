package com.example.week7project.service;

import com.example.week7project.domain.ChatRoom;
import com.example.week7project.domain.Member;
import com.example.week7project.domain.Post;
import com.example.week7project.dto.request.ChatRoomRequestDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.repository.ChatRoomRepository;
import com.example.week7project.repository.PostRepository;
import com.example.week7project.security.TokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ObjectMapper objectMapper;
    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ResponseDto<?> createRoom(Long postId, ChatRoomRequestDto requestDto, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if(!chkResponse.isSuccess())
            return ResponseDto.fail(chkResponse);
        Member member = validateMember(request);
        if(member == null)
            return ResponseDto.fail("사용자를 찾을 수 없습니다.");

        Optional<Post> getPost = postRepository.findById(postId);
        if(getPost.isEmpty())
            return ResponseDto.fail("게시글을 찾을 수 없습니다.");
//        Post post = Post.builder()
//                .title("뭐팝니다")
//                .category("전자")
//                .content("내용")
//                .status("판매중")
//                .price(10000)
//                .member(member)
//                .build();
//        postRepository.save(post);

        ChatRoom chatRoom = ChatRoom.builder()
                .name(requestDto.getRoomName())
                .post(getPost.get())
//                .post(post)
                .member(member)
                .build();
        chatRoomRepository.save(chatRoom);
        return ResponseDto.success(chatRoom);
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try{
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private ResponseDto<?> validateCheck(HttpServletRequest request) {
        if(null == request.getHeader("RefreshToken") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }
        Member member = validateMember(request);
        if(null == member) {
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