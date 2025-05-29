package com.example.rightbackend.rekognition.controller.dto.detection;

import java.util.Map;

public record MetaData(
        String requestId,
        Integer httpStatusCode,
        Map<String, String> httpHeaders,
        Integer retryAttempts) {
}