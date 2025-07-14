package com.example.rightbackend.noti.domain.repository;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.noti.domain.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {
    Optional<FCMToken> findByMember(Member member);
}