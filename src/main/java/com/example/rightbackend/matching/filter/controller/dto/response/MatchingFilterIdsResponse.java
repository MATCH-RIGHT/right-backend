package com.example.rightbackend.matching.filter.controller.dto.response;

import com.example.rightbackend.matching.filter.domain.MatchingFilter;
import com.example.rightbackend.matching.filter.domain.Region;
import com.example.rightbackend.matching.filter.service.IdealFaceFeatureUtil;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public record MatchingFilterIdsResponse(
        Long id,
        Integer minAge,
        Integer maxAge,
        BigInteger idealFaceFeaturesBitmask,
        Map<String, List<Integer>> idealFaceFeatureIds,
        List<FeatureWithId> idealFaceFeatures,
        RegionDto region
) {
    public static MatchingFilterIdsResponse from(MatchingFilter matchingFilter) {
        RegionDto regionDto = null;
        if (matchingFilter.getRegion() != null) {
            regionDto = new RegionDto(
                    matchingFilter.getRegion().getId(),
                    matchingFilter.getRegion().getName()
            );
        }

        Map<String, List<Integer>> featureIds = IdealFaceFeatureUtil.convertBitmaskToIds(matchingFilter.getIdealFaceFeaturesBitmask());

        List<FeatureWithId> featuresWithIds = IdealFaceFeatureUtil.getFeatureInfosFromBitmask(matchingFilter.getIdealFaceFeaturesBitmask())
                .stream()
                .map(info -> new FeatureWithId(
                        info.category(),
                        info.code(),
                        info.name(),
                        info.index(),
                        info.featureId()
                ))
                .toList();

        return new MatchingFilterIdsResponse(
                matchingFilter.getId(),
                matchingFilter.getMinAge(),
                matchingFilter.getMaxAge(),
                matchingFilter.getIdealFaceFeaturesBitmask(),
                featureIds,
                featuresWithIds,
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

    public record FeatureWithId(
            String category,
            String code,
            String name,
            int index,
            int featureId
    ) {}
}