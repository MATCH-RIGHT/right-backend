package com.example.rightbackend.matching.filter.domain.repository;

import com.example.rightbackend.matching.filter.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByName(String name);
    Optional<Region> findByCode(String code);
}
