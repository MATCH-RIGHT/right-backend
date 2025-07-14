package com.example.rightbackend.matching.business.controller.dto.response;

import com.example.rightbackend.image.controller.dto.response.ImageListResponse;
import com.example.rightbackend.image.service.ImageService;
import com.example.rightbackend.matching.business.domain.Matched;
import com.example.rightbackend.member.domain.MemberProfile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record MatchedResponse(
        Long matchedId,
        MemberProfileDto memberProfile,
        Integer compatibilityScore,
        String matchingType,
        LocalDateTime createdAt
) {

    public static MatchedResponse from(Matched matched, Long currentMemberId) {
        MemberProfile otherProfile;
        
        if (matched.getMemberProfile1().getId().equals(currentMemberId)) {
            otherProfile = matched.getMemberProfile2();
        } else {
            otherProfile = matched.getMemberProfile1();
        }
        
        return new MatchedResponse(
                matched.getId(),
                MemberProfileDto.from(otherProfile),
                matched.getCompatibilityScore(),
                matched.getMatchingType(),
                matched.getCreatedAt()
        );
    }
    
    public static MatchedResponse from(Matched matched, Long currentMemberId, ImageService imageService) {
        MemberProfile otherProfile;
        
        if (matched.getMemberProfile1().getId().equals(currentMemberId)) {
            otherProfile = matched.getMemberProfile2();
        } else {
            otherProfile = matched.getMemberProfile1();
        }
        
        return new MatchedResponse(
                matched.getId(),
                MemberProfileDto.from(otherProfile, imageService),
                matched.getCompatibilityScore(),
                matched.getMatchingType(),
                matched.getCreatedAt()
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
            List<ImageListResponse> images
    ) {

        public static MemberProfileDto from(MemberProfile memberProfile) {
            String location = memberProfile.getMemberProfileToLocations().isEmpty() ? 
                    null : memberProfile.getMemberProfileToLocations().get(0).getLocation().getName();
            
            List<ImageListResponse> images = new ArrayList<>();
            
            return new MemberProfileDto(
                    memberProfile.getId(),
                    memberProfile.getNickname(),
                    memberProfile.getGender(),
                    memberProfile.getAge(),
                    location,
                    memberProfile.getHeight(),
                    memberProfile.getBody_type(),
                    memberProfile.getJob(),
                    images
            );
        }
        
        public static MemberProfileDto from(MemberProfile memberProfile, ImageService imageService) {
            String location = memberProfile.getMemberProfileToLocations().isEmpty() ? 
                    null : memberProfile.getMemberProfileToLocations().get(0).getLocation().getName();
            
            List<ImageListResponse> images = imageService.getMemberImages(memberProfile.getId());
            
            return new MemberProfileDto(
                    memberProfile.getId(),
                    memberProfile.getNickname(),
                    memberProfile.getGender(),
                    memberProfile.getAge(),
                    location,
                    memberProfile.getHeight(),
                    memberProfile.getBody_type(),
                    memberProfile.getJob(),
                    images
            );
        }
    }
}
