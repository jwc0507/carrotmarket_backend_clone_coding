package com.example.week7project.repository;

import com.example.week7project.domain.ChatRoom;
import com.example.week7project.domain.Member;
import com.example.week7project.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByMemberAndPost(Member member, Post post);

    List<ChatRoom> findByPost(Post post);

    List<ChatRoom> findByMember(Member member);
}
