package com.example.week7project.repository;

import com.example.week7project.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByMemberId(Long memberId);
    List<Post> findAllByOrderByCreatedAtDesc();
}
