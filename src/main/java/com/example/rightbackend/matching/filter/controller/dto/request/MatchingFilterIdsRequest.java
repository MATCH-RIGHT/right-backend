package com.example.rightbackend.matching.filter.controller.dto.request;

import java.util.List;
import java.util.Map;

public record MatchingFilterIdsRequest(
        Integer minAge,
        Integer maxAge,
        Map<String, List<Integer>> idealFaceFeatureIds,
        Long regionId
) {
}