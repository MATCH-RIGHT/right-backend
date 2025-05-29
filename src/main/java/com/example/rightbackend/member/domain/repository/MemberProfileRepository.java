package com.example.rightbackend.member.domain.repository;

import com.example.rightbackend.member.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {
    
    List<MemberProfile> findByGender(String gender);
    
    @Query("SELECT mp FROM MemberProfile mp WHERE mp.gender = :gender AND FUNCTION('YEAR', CURRENT_DATE) - FUNCTION('YEAR', FUNCTION('STR_TO_DATE', mp.birthday, '%Y-%m-%d')) + 1 BETWEEN :minAge AND :maxAge")
    List<MemberProfile> findByGenderAndAgeRange(
            @Param("gender") String gender,
            @Param("minAge") int minAge,
            @Param("maxAge") int maxAge);
}