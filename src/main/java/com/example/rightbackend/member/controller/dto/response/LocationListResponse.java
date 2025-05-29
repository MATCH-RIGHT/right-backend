package com.example.rightbackend.member.controller.dto.response;

import com.example.rightbackend.member.domain.Location;

import java.util.List;
import java.util.stream.Collectors;

public record LocationListResponse(
    List<LocationDto> locations
) {
    public static LocationListResponse from(List<Location> locations) {
        List<LocationDto> locationDtos = locations.stream()
                .map(location -> new LocationDto(location.getId(), location.getName()))
                .collect(Collectors.toList());
        return new LocationListResponse(locationDtos);
    }
    
    public record LocationDto(Long id, String name) {}
}
