package com.example.rightbackend.matching.business.controller.dto.response;

import com.example.rightbackend.image.controller.dto.response.ImageListResponse;
import com.example.rightbackend.image.service.ImageService;
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
    
    public static MatchingResultResponse from(MatchingResult matchingResult, Long currentMemberId, ImageService imageService) {
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
                MemberProfileDto.from(otherProfile, imageService),
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
            Integer height,
            String bodyType,
            String job,
            List<ImageListResponse> images
    ) {

        public static MemberProfileDto from(MemberProfile memberProfile) {
            String location = memberProfile.getMemberProfileToLocations().isEmpty() ? 
                    null : memberProfile.getMemberProfileToLocations().get(0).getLocation().getName();
            
            List<ImageListResponse> images = new ArrayList<>();
            
            return new MemberProfileDto(
                    memberProfile.getId(),
                    memberProfile.getDecryptedNickname(),
                    memberProfile.getDecryptedGender(),
                    memberProfile.getAge(),
                    location,
                    memberProfile.getDecryptedHeight(),
                    memberProfile.getDecryptedBodyType(),
                    memberProfile.getDecryptedJob(),
                    images
            );
        }
        
        public static MemberProfileDto from(MemberProfile memberProfile, ImageService imageService) {
            String location = memberProfile.getMemberProfileToLocations().isEmpty() ? 
                    null : memberProfile.getMemberProfileToLocations().get(0).getLocation().getName();
            
            List<ImageListResponse> images = imageService.getMemberImages(memberProfile.getId());
            
            return new MemberProfileDto(
                    memberProfile.getId(),
                    memberProfile.getDecryptedNickname(),
                    memberProfile.getDecryptedGender(),
                    memberProfile.getAge(),
                    location,
                    memberProfile.getDecryptedHeight(),
                    memberProfile.getDecryptedBodyType(),
                    memberProfile.getDecryptedJob(),
                    images
            );
        }
    }
}