package com.example.week7project.service;

import com.example.week7project.domain.ChatMessage;
import com.example.week7project.domain.ChatRoom;
import com.example.week7project.domain.Member;
import com.example.week7project.domain.Post;
import com.example.week7project.dto.request.ChatRoomRequestDto;
import com.example.week7project.dto.response.ChatMsgResponseDto;
import com.example.week7project.dto.response.MyChatDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.repository.ChatMessageRepository;
import com.example.week7project.repository.ChatRoomRepository;
import com.example.week7project.repository.MemberRepository;
import com.example.week7project.repository.PostRepository;
import com.example.week7project.security.TokenProvider;
import com.example.week7project.time.Time;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ObjectMapper objectMapper;
    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 채팅방 개설
    // 내 게시글에 방만들지 못하게 수정
    @Transactional
    public ResponseDto<?> createRoom(Long postId, ChatRoomRequestDto requestDto, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return ResponseDto.fail(chkResponse);
        Member member = (Member) chkResponse.getData();
        if (member == null)
            return ResponseDto.fail("사용자를 찾을 수 없습니다.");

        Member updateMember = memberRepository.findByNickname(member.getNickname()).get(); // 판매자
        Optional<Post> getPost = postRepository.findById(postId);
        if (getPost.isEmpty())
            return ResponseDto.fail("게시글을 찾을 수 없습니다.");
        if(!getPost.get().validateMember(updateMember))
            return ResponseDto.fail("자신의 글에 채팅을 걸 수 없습니다.");

        ChatRoom chatRoom = ChatRoom.builder()
                .name(requestDto.getRoomName())
                .post(getPost.get())
                .member(member)
                .build();
        chatRoomRepository.save(chatRoom);

        getPost.get().addChatCount();

        return ResponseDto.success(chatRoom);
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
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

    // 자신이 속한 게시글의 채팅방 번호 찾기.
    public ResponseDto<?> getRoomId(Long postId, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return ResponseDto.fail(chkResponse);
        Member member = (Member) chkResponse.getData();

        Member updateMember = memberRepository.findByNickname(member.getNickname()).get();

        Optional<Post> getPost = postRepository.findById(postId);
        if (getPost.isEmpty())
            return ResponseDto.fail("게시글을 찾을 수 없습니다.");


        ChatRoom chatRoom = chatRoomRepository.findByMemberAndPost(updateMember, getPost.get());
        if (chatRoom == null)
            return ResponseDto.success(0);
        return ResponseDto.success(chatRoom.getId());
    }

    /**
     * 회원 채팅방 조회
     * 중복된 방이 있으면 넣지 않기
     */
    public ResponseDto<?> getChatRooms(HttpServletRequest request) {
        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);

        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = (Member) chkResponse.getData();
        Member updateMember = memberRepository.findByNickname(member.getNickname()).get();

        List<MyChatDto> chatDtoList = new ArrayList<>();            // 채팅방 리스트 담을 용도
        // 내가 쓴 글 기준으로 찾아오기
        List<Post> postList = postRepository.findByMemberId(updateMember.getId());
        for (Post post : postList) {
            if (chatRoomRepository.findByPost(post) != null) {
                List<ChatRoom> chatRoomList = chatRoomRepository.findByPost(post);
                for (ChatRoom chatRoom : chatRoomList) {
                    Long chatRoomId = chatRoom.getId();
                    Member buyer = chatRoom.getMember();
                    String address = buyer.getAddress();
                    String buyerNickname = buyer.getNickname();
                    String message;
                    String lastTime;
                    // == 채팅 내역 가져와야 함.
                    if (chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom).isEmpty()) {
                        message = "메세지가 없습니다.";
                        lastTime = "0초전";
                    } else {
                        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom);
                        message = chatMessageList.get(0).getMessage();
                        LocalDateTime getTime = chatMessageList.get(0).getCreatedAt();
                        lastTime = Time.convertLocaldatetimeToTime(getTime);
                    }

                    chatDtoList.add(
                            MyChatDto.builder()
                                    .id(chatRoomId)
                                    .roomName(chatRoom.getName())
                                    .nickName(buyerNickname)
                                    .address(address)
                                    .message(message)
                                    .lastTime(lastTime)
                                    .build()
                    );
                }
            }
        }

        // 내가 구매한 기준으로 찾아오기
        List<ChatRoom> chatRooms = chatRoomRepository.findByMember(updateMember);
        for (ChatRoom chatRoom : chatRooms) {
            Long chatRoomId = chatRoom.getId();
            Member seller = chatRoom.getPost().getMember();
            if(seller.getId().equals(updateMember.getId()))
                continue;
            String sellerNickname = seller.getNickname();
            String address = seller.getAddress();
            String message;
            String lastTime;
            //== 채팅 내역 가져와야 함.
            if (chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom).isEmpty()) {
                message = "메세지가 없습니다.";
                lastTime = "0초전";
            } else {
                List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom);
                message = chatMessageList.get(0).getMessage();
                LocalDateTime getTime = chatMessageList.get(0).getCreatedAt();
                lastTime = Time.convertLocaldatetimeToTime(getTime);
            }
            chatDtoList.add(
                    MyChatDto.builder()
                            .id(chatRoomId)
                            .roomName(chatRoom.getName())
                            .nickName(sellerNickname)
                            .address(address)
                            .message(message)
                            .lastTime(lastTime)
                            .build()
            );
        }
        return ResponseDto.success(chatDtoList);
    }

    // 채팅방 메세지들 불러오기
    public ResponseDto<?> getMessage(Long roomId, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        Optional<ChatRoom> getChatRoom = chatRoomRepository.findById(roomId);
        ChatRoom chatRoom;
        if (getChatRoom.isPresent())
            chatRoom = getChatRoom.get();
        else
            return ResponseDto.fail("채팅방을 찾을 수 없습니다.");

        Long buyerId = chatRoom.getMember().getId();
        String type;

        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoomOrderByCreatedAtDesc(chatRoom);
        List<ChatMsgResponseDto> chatMsgResponseDtos = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessageList) {
            Member member = chatMessage.getMember();
            if (member.getId().equals(buyerId))
                type = "구매자";
            else
                type = "판매자";
            ChatMsgResponseDto chatMsgResponseDto = ChatMsgResponseDto.builder()
                    .type(type)
                    .nickname(member.getNickname())
                    .message(chatMessage.getMessage())
                    .build();
            chatMsgResponseDtos.add(chatMsgResponseDto);
        }
        return ResponseDto.success(chatMsgResponseDtos);
    }

}