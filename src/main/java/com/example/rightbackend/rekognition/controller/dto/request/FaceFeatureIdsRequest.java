package com.example.rightbackend.rekognition.controller.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FaceFeatureIdsRequest {
    private Map<String, List<Integer>> featureIds;
}