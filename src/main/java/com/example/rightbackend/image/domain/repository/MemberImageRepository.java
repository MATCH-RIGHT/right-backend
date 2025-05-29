package com.example.rightbackend.image.domain.repository;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.image.domain.MemberImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberImageRepository extends JpaRepository<MemberImage, Long> {
    Optional<MemberImage> findByName(String name);
    List<MemberImage> findByMember(Member member);
}