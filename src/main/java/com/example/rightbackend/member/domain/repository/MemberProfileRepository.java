package com.example.rightbackend.member.domain.repository;

import com.example.rightbackend.member.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {
}