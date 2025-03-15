package com.example.rightbackend.auth.domain.repository;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByMember(Member member);
    void deleteByMember(Member member);
}