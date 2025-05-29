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

    /**
     * 소스 사용자가 좋아요를 보냄
     */
    public void sourceLike() {
        this.sourceLiked = true;
        checkMatch();
    }

    /**
     * 타겟 사용자가 좋아요를 보냄
     */
    public void targetLike() {
        this.targetLiked = true;
        checkMatch();
    }

    /**
     * 매칭 여부 확인
     */
    private void checkMatch() {
        if (sourceLiked && targetLiked) {
            this.matched = true;
        }
    }

    /**
     * 매칭이 만료되었는지
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}