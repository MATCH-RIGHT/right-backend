package com.example.rightbackend.matching.business.controller.dto.response;

import com.example.rightbackend.matching.business.domain.MatchingResult;
import com.example.rightbackend.member.domain.MemberProfile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record MatchingResultResponse(
        Long matchingResultId,
        MemberProfileDto memberProfile,
        Integer compatibilityScore,
        String matchingType,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        boolean liked,
        boolean matched
) {

    public static MatchingResultResponse from(MatchingResult matchingResult, Long currentMemberId) {
        MemberProfile otherProfile;
        boolean liked;
        
        if (matchingResult.getSourceMemberProfile().getId().equals(currentMemberId)) {
            otherProfile = matchingResult.getTargetMemberProfile();
            liked = matchingResult.isSourceLiked();
        } else {
            otherProfile = matchingResult.getSourceMemberProfile();
            liked = matchingResult.isTargetLiked();
        }
        
        return new MatchingResultResponse(
                matchingResult.getId(),
                MemberProfileDto.from(otherProfile),
                matchingResult.getCompatibilityScore(),
                matchingResult.getMatchingType(),
                matchingResult.getCreatedAt(),
                matchingResult.getExpiresAt(),
                liked,
                matchingResult.isMatched()
        );
    }
    
    public record MemberProfileDto(
            Long id,
            String nickname,
            String gender,
            Integer age,
            String location,
            String height,
            String bodyType,
            String job,
            List<String> imageUrls
    ) {

        public static MemberProfileDto from(MemberProfile memberProfile) {
            String location = memberProfile.getMemberProfileToLocations().isEmpty() ? 
                    null : memberProfile.getMemberProfileToLocations().get(0).getLocation().getName();
            
            List<String> imageUrls = new ArrayList<>();
            
            return new MemberProfileDto(
                    memberProfile.getId(),
                    memberProfile.getNickname(),
                    memberProfile.getGender(),
                    memberProfile.getAge(),
                    location,
                    memberProfile.getHeight(),
                    memberProfile.getBody_type(),
                    memberProfile.getJob(),
                    imageUrls
            );
        }
    }
}