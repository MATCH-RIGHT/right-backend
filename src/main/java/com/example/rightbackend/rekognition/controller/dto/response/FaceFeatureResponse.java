package com.example.rightbackend.rekognition.controller.dto.response;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class FaceFeatureResponse {
    private final List<FeatureDto> features;

    public FaceFeatureResponse(List<Map<String, Object>> featuresWithId) {
        this.features = featuresWithId.stream()
                .map(map -> new FeatureDto(
                        (Long) map.get("id"),
                        (String) map.get("name"),
                        (String) map.get("featureType"),
                        (Integer) map.get("featureValueId")))
                .collect(Collectors.toList());
    }

    @Getter
    public static class FeatureDto {
        private final Long id;
        private final String name;
        private final String featureType;
        private final Integer featureValueId;

        public FeatureDto(Long id, String name, String featureType, Integer featureValueId) {
            this.id = id;
            this.name = name;
            this.featureType = featureType;
            this.featureValueId = featureValueId;
        }
    }
}