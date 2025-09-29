package com.example.rightbackend.rekognition.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.List;

@Getter
@AllArgsConstructor
public class FaceFeatureIdsResponse {
    private final Map<String, List<Integer>> featureIds;
}