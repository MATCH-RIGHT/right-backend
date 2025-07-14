package com.example.rightbackend.matching.business.domain.repository;

import com.example.rightbackend.matching.business.domain.Matched;
import com.example.rightbackend.member.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchedRepository extends JpaRepository<Matched, Long> {

    @Query("SELECT m FROM Matched m WHERE " +
           "m.memberProfile1 = :memberProfile OR m.memberProfile2 = :memberProfile")
    List<Matched> findByMemberProfile(@Param("memberProfile") MemberProfile memberProfile);
    
    @Query("SELECT m FROM Matched m WHERE " +
           "m.memberProfile1.id = :memberId OR m.memberProfile2.id = :memberId")
    List<Matched> findByMemberId(@Param("memberId") Long memberId);
}
