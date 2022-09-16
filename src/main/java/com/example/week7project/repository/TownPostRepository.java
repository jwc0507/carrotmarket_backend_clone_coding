package com.example.week7project.repository;

import com.example.week7project.domain.TownPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TownPostRepository extends JpaRepository<TownPost, Long> {
    List<TownPost> findByOrderByCreatedAtDesc();
}
