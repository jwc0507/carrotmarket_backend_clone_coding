package com.example.week7project.webSocket;

import com.example.week7project.domain.ChatMessage;
import com.example.week7project.domain.ChatRoom;
import com.example.week7project.domain.Member;
import com.example.week7project.dto.request.ChatMessageDto;
import com.example.week7project.dto.response.SendMessageDto;
import com.example.week7project.repository.ChatMessageRepository;
import com.example.week7project.repository.ChatRoomRepository;
import com.example.week7project.repository.MemberRepository;
import com.example.week7project.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
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
        else {
            System.out.println("채팅방 id 입력 오류");
            return;
        }

        // 발신자 찾기
        Optional<?> getMember = memberRepository.findByNickname(chatMessagedto.getSender());
        if(getMember.isEmpty()) {
            System.out.println("발신자 닉네임 입력 오류");
            return;
        }
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

        for (Map<String, Object> mapSessionList : sessionList) {
            Long room_id = (Long) mapSessionList.get("room_id");
            WebSocketSession sess = (WebSocketSession) mapSessionList.get("session");

            if (room_id.equals(roomId)) {
                SendMessageDto.builder()
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

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //소켓 종료
        for (Map<String, Object> list : sessionList) {
            WebSocketSession sess = (WebSocketSession) list.get("session");
            if (sess.equals(session)) {
                sessionList.remove(list);
            }
        }
        super.afterConnectionClosed(session, status);
    }

}