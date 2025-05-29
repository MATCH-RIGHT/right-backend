package com.example.rightbackend.rekognition.controller.dto.response;

import com.example.rightbackend.rekognition.domain.FaceFeature;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class FaceFeatureListResponse {
    private final List<FaceFeatureDto> faceFeatures;

    public FaceFeatureListResponse(List<FaceFeature> faceFeatures) {
        this.faceFeatures = faceFeatures.stream()
                .map(FaceFeatureDto::new)
                .collect(Collectors.toList());
    }

    @Getter
    public static class FaceFeatureDto {
        private final Long id;
        private final String name;

        public FaceFeatureDto(FaceFeature faceFeature) {
            this.id = faceFeature.getId();
            this.name = faceFeature.getName();
        }
    }
}
