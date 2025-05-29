package com.example.rightbackend.image.domain.repository;

import com.example.rightbackend.image.domain.MemberImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberImageRepository extends JpaRepository<MemberImage, Long> {
    Optional<MemberImage> findByName(String name);
}