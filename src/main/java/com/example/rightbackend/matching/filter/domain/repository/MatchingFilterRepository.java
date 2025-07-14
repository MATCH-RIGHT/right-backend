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
    
    @Query("SELECT mf FROM MatchingFilter mf " +
           "JOIN mf.memberProfile mp " +
           "WHERE mp.gender = :gender " +
           "ORDER BY mp.id")
    List<MatchingFilter> findByMemberProfileGender(String gender);
    
    @Query("SELECT mf FROM MatchingFilter mf " +
           "JOIN mf.memberProfile mp " +
           "LEFT JOIN mp.memberProfileToLocations mpl " +
           "LEFT JOIN mpl.location loc " +
           "WHERE mp.gender = :gender AND " +
           "(loc.name = :regionPartition OR loc IS NULL) " +
           "ORDER BY CASE WHEN loc.name = :regionPartition THEN 0 ELSE 1 END, mp.id")
    List<MatchingFilter> findByMemberProfileGenderAndRegionPriority(String gender, String regionPartition);
}