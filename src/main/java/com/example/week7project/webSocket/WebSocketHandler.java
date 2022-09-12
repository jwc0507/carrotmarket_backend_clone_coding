package com.example.week7project.webSocket;

import com.example.week7project.domain.ChatMessage;
import com.example.week7project.domain.ChatRoom;
import com.example.week7project.domain.Member;
import com.example.week7project.dto.request.ChatMessageDto;
import com.example.week7project.dto.response.ChatMessageResponseDto;
import com.example.week7project.repository.ChatMessageRepository;
import com.example.week7project.repository.ChatRoomRepository;
import com.example.week7project.repository.MemberRepository;
import com.example.week7project.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private List<Map<String, Object>> sessionList = new ArrayList<>();

    @Override
    @Transactional
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("{}", payload);
        ChatMessageDto chatMessagedto = objectMapper.readValue(payload, ChatMessageDto.class);

        // 채팅방 찾기
        Optional<?> getChatRoom = chatRoomRepository.findById(chatMessagedto.getRoomId());
        ChatRoom chatRoom;
        if (getChatRoom.isPresent())
            chatRoom = (ChatRoom) getChatRoom.get();
        else
            return;

        // 수신자 찾기
        Optional<?> getMember = memberRepository.findByNickname(chatMessagedto.getSender());
        if(getMember.isEmpty())
            return;
        Member sender = (Member) getMember.get();

        Map<String, Object> map;
        Long roomId = chatMessagedto.getRoomId();

        boolean exist = false;

        for (Map<String, Object> mapSessionList : sessionList) {
            WebSocketSession sess = (WebSocketSession) mapSessionList.get("session");
            if(sess.getId().equals(session.getId()))
                exist = true;
        }

        if (!exist) {
            map = new HashMap<>();
            map.put("room_id", chatMessagedto.getRoomId());
            map.put("session", session);
            sessionList.add(map);
        }

//        Optional<?> getChatRoom = chatRoomRepository.findById(chatMessagedto.getRoomId());
//        ChatRoom chatRoom;
//        if (getChatRoom.isPresent()) {
//            chatRoom = (ChatRoom) getChatRoom.get();
        //챗룸의 판매자와 구매자가 맞는지 확인한다.


//            chatRoom = (ChatRoom) getChatRoom.get();
//            ChatRoomDto chatRoomDto = ChatRoomDto.builder()
//                    .roomId(chatRoom.getId())
//                    .name(chatRoom.getName())
//                    .build();
//            chatRoomDto.handlerActions(session, chatMessagedto, chatService);


        for (Map<String, Object> mapSessionList : sessionList) {
            Long room_id = (Long) mapSessionList.get("room_id");
            WebSocketSession sess = (WebSocketSession) mapSessionList.get("session");

            if (room_id.equals(roomId)) {
                ChatMessageResponseDto.builder()
            //            .messageType(chatMessagedto.getMessageType())
                        .roomId(chatRoom.getId())
                        .postId(chatRoom.getPost().getId())
                        .sender(sender.getNickname())
                        .message(chatMessagedto.getMessage())
                        .build();
                chatService.sendMessage(sess, chatMessagedto);
            }
        }
        ChatMessage chatMessage = ChatMessage.builder()
                .member(sender)
                .chatRoom(chatRoom)
                .message(chatMessagedto.getMessage())
                .build();
        chatMessageRepository.save(chatMessage);
        log.info(chatMessagedto.getSender()+" :  "+chatMessagedto.getMessage());
    }

//    private ResponseDto<?> validateCheck(ChatMessageDto request) {
//        if (null == request.getRefreshToken() || null == request.getAuthorization()) {
//            return ResponseDto.fail("로그인이 필요합니다.");
//        }
//        Member member = validateMember(request.getRefreshToken());
//        if (null == member) {
//            return ResponseDto.fail("Token이 유효하지 않습니다.");
//        }
//        return ResponseDto.success(member);
//    }
//
//    @Transactional
//    public Member validateMember(String token) {
//        if (!tokenProvider.validateToken(token)) {
//            return null;
//        }
//        return tokenProvider.getMemberFromAuthentication();
//    }

}