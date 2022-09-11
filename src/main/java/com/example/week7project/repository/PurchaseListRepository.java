package com.example.week7project.repository;

import com.example.week7project.domain.PurchaseList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseListRepository extends JpaRepository<PurchaseList, Long> {

    List<PurchaseList> findByMemberId(Long memberId);

}
