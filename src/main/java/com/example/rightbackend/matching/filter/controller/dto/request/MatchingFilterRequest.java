package com.example.rightbackend.matching.filter.controller.dto.request;

import java.util.List;

public record MatchingFilterRequest(
        Integer minAge,
        Integer maxAge,
        List<String> idealFaceFeatures,
        Long regionId
) {
}
