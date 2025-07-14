package com.example.rightbackend.matching.business.domain;

import com.example.rightbackend.member.domain.MemberProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "matched")
public class Matched {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matched_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_profile_id_1")
    private MemberProfile memberProfile1;

    @ManyToOne
    @JoinColumn(name = "member_profile_id_2")
    private MemberProfile memberProfile2;

    @Column(name = "compatibility_score")
    private Integer compatibilityScore;

    @Column(name = "matching_type")
    private String matchingType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "matched_at")
    private LocalDateTime matchedAt;

    protected Matched() {}

    public static Matched fromMatchingResult(MatchingResult matchingResult) {
        Matched matched = new Matched();
        matched.memberProfile1 = matchingResult.getSourceMemberProfile();
        matched.memberProfile2 = matchingResult.getTargetMemberProfile();
        matched.compatibilityScore = matchingResult.getCompatibilityScore();
        matched.matchingType = matchingResult.getMatchingType();
        matched.createdAt = matchingResult.getCreatedAt();
        matched.matchedAt = LocalDateTime.now();
        return matched;
    }
    
    public MemberProfile getSourceMemberProfile() {
        return memberProfile1;
    }
    
    public MemberProfile getTargetMemberProfile() {
        return memberProfile2;
    }
}