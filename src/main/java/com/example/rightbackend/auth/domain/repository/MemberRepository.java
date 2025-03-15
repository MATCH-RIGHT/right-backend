package com.example.rightbackend.auth.domain.repository;

import com.example.rightbackend.auth.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderId(String providerId);
    Optional<Member> findById(Long id);
    Optional<Member> findFirstByNameAndPhoneNumber(String name, String phoneNumber);
}