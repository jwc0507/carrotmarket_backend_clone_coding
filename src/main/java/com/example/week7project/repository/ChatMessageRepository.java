package com.example.week7project.repository;

import com.example.week7project.domain.ChatMessage;
import com.example.week7project.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);
    List<ChatMessage> findByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);
}
