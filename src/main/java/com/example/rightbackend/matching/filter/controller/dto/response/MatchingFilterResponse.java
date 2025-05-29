package com.example.rightbackend.matching.filter.controller.dto.response;

import com.example.rightbackend.matching.filter.domain.MatchingFilter;
import com.example.rightbackend.matching.filter.domain.Region;
import com.example.rightbackend.matching.filter.service.IdealFaceFeatureUtil;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public record MatchingFilterResponse(
        Long id,
        Integer minAge,
        Integer maxAge,
        BigInteger idealFaceFeaturesBitmask,
        List<FaceFeatureDto> idealFaceFeatures,
        RegionDto region
) {
    public static MatchingFilterResponse from(MatchingFilter matchingFilter) {
        RegionDto regionDto = null;
        if (matchingFilter.getRegion() != null) {
            regionDto = new RegionDto(
                    matchingFilter.getRegion().getId(),
                    matchingFilter.getRegion().getName()
            );
        }

        List<FaceFeatureDto> faceFeatureDtos = IdealFaceFeatureUtil.getFeatureInfosFromBitmask(matchingFilter.getIdealFaceFeaturesBitmask())
                .stream()
                .map(featureInfo -> new FaceFeatureDto(
                        featureInfo.category(),
                        featureInfo.code(),
                        featureInfo.name(),
                        featureInfo.index()
                ))
                .collect(Collectors.toList());

        return new MatchingFilterResponse(
                matchingFilter.getId(),
                matchingFilter.getMinAge(),
                matchingFilter.getMaxAge(),
                matchingFilter.getIdealFaceFeaturesBitmask(),
                faceFeatureDtos,
                regionDto
        );
    }

    public record RegionDto(
            Long id,
            String name
    ) {
        public static RegionDto from(Region region) {
            return new RegionDto(
                    region.getId(),
                    region.getName()
            );
        }
    }

    public record FaceFeatureDto(
            String category,
            String code,
            String name,
            int index
    ) {}
}