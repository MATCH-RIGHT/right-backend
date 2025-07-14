package com.example.rightbackend.matching.business.domain;

import com.example.rightbackend.member.domain.MemberProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "matching_result")
public class MatchingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_result_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_member_profile_id")
    private MemberProfile sourceMemberProfile;

    @ManyToOne
    @JoinColumn(name = "target_member_profile_id")
    private MemberProfile targetMemberProfile;

    @Column(name = "compatibility_score")
    private Integer compatibilityScore;

    @Column(name = "matching_type")
    private String matchingType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "source_liked")
    private boolean sourceLiked = false;

    @Column(name = "target_liked")
    private boolean targetLiked = false;

    @Column(name = "matched")
    private boolean matched = false;

    protected MatchingResult() {}

    public static MatchingResult of(MemberProfile sourceMemberProfile, MemberProfile targetMemberProfile, 
                                   Integer compatibilityScore, String matchingType) {
        MatchingResult result = new MatchingResult();
        result.sourceMemberProfile = sourceMemberProfile;
        result.targetMemberProfile = targetMemberProfile;
        result.compatibilityScore = compatibilityScore;
        result.matchingType = matchingType;
        result.createdAt = LocalDateTime.now();
        result.expiresAt = LocalDateTime.now().plusDays(3);
        return result;
    }

    public void sourceLike() {
        this.sourceLiked = true;
        checkMatch();
    }

    public void targetLike() {
        this.targetLiked = true;
        checkMatch();
    }

    private void checkMatch() {
        if (sourceLiked && targetLiked) {
            this.matched = true;
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isSourceLiked() {
        return sourceLiked;
    }
    
    public boolean isTargetLiked() {
        return targetLiked;
    }
}