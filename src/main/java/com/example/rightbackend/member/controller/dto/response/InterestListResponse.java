package com.example.rightbackend.member.controller.dto.response;

import com.example.rightbackend.member.domain.Interest;

import java.util.List;
import java.util.stream.Collectors;

public record InterestListResponse(
    List<InterestDto> interests
) {
    public static InterestListResponse from(List<Interest> interests) {
        List<InterestDto> interestDtos = interests.stream()
                .map(interest -> new InterestDto(interest.getId(), interest.getIcon(), interest.getName()))
                .collect(Collectors.toList());
        return new InterestListResponse(interestDtos);
    }
    
    public record InterestDto(Long id, String icon, String label) {}
}
