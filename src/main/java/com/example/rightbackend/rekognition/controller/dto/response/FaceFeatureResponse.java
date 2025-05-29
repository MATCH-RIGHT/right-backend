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
                .map(map -> new FeatureDto((Long) map.get("id"), (String) map.get("name")))
                .collect(Collectors.toList());
    }
    
    @Getter
    public static class FeatureDto {
        private final Long id;
        private final String name;
        
        public FeatureDto(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}