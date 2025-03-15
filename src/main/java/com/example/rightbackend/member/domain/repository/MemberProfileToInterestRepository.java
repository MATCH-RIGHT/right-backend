package com.example.rightbackend.member.domain.repository;

import com.example.rightbackend.member.domain.MemberProfileToInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberProfileToInterestRepository extends JpaRepository<MemberProfileToInterest, Long> {
}