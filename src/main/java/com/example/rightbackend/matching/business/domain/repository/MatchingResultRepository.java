package com.example.rightbackend.matching.business.domain.repository;

import com.example.rightbackend.matching.business.domain.MatchingResult;
import com.example.rightbackend.member.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchingResultRepository extends JpaRepository<MatchingResult, Long> {

    @Query("SELECT mr FROM MatchingResult mr WHERE " +
           "(mr.sourceMemberProfile = :memberProfile OR mr.targetMemberProfile = :memberProfile) " +
           "AND mr.expiresAt > :now")

    List<MatchingResult> findActiveMatchingsByMemberProfile(
            @Param("memberProfile") MemberProfile memberProfile,
            @Param("now") LocalDateTime now);

    @Query("SELECT mr FROM MatchingResult mr WHERE " +
           "((mr.sourceMemberProfile = :profile1 AND mr.targetMemberProfile = :profile2) OR " +
           "(mr.sourceMemberProfile = :profile2 AND mr.targetMemberProfile = :profile1)) " +
           "AND mr.expiresAt > :now")
           
    Optional<MatchingResult> findActiveMatchingBetweenProfiles(
            @Param("profile1") MemberProfile memberProfile1,
            @Param("profile2") MemberProfile memberProfile2,
            @Param("now") LocalDateTime now);

    List<MatchingResult> findByExpiresAtLessThanAndMatchedFalse(LocalDateTime now);

    @Query("SELECT mr FROM MatchingResult mr WHERE " +
           "(mr.sourceMemberProfile = :memberProfile OR mr.targetMemberProfile = :memberProfile) " +
           "AND mr.matched = true")
    List<MatchingResult> findMatchedResults(@Param("memberProfile") MemberProfile memberProfile);
}
