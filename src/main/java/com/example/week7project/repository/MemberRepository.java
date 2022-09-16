package com.example.week7project.repository;

import com.example.week7project.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickname(String nickName);
    Optional<Member> findByPhoneNumber(String phoneNumber);
}
