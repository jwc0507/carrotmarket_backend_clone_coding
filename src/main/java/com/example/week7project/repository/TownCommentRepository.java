package com.example.week7project.repository;

import com.example.week7project.domain.TownComment;
import com.example.week7project.domain.TownPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TownCommentRepository extends JpaRepository<TownComment, Long> {

    List<TownComment> findByTownPost(TownPost townPost);
}
