package com.example.rightbackend.matching.filter.domain.repository;

import com.example.rightbackend.matching.filter.domain.MatchingFilter;
import com.example.rightbackend.member.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchingFilterRepository extends JpaRepository<MatchingFilter, Long> {
    
    Optional<MatchingFilter> findByMemberProfile(MemberProfile memberProfile);
    
    List<MatchingFilter> findByGenderPartition(String genderPartition);
    
    List<MatchingFilter> findByGenderPartitionAndRegionPartition(String genderPartition, String regionPartition);
    
    @Query("SELECT mf FROM MatchingFilter mf WHERE mf.genderPartition = :genderPartition AND " +
           "(mf.regionPartition = :regionPartition OR mf.regionPartition IS NULL) " +
           "ORDER BY CASE WHEN mf.regionPartition = :regionPartition THEN 0 ELSE 1 END")
    List<MatchingFilter> findByGenderPartitionWithRegionPriority(String genderPartition, String regionPartition);
}