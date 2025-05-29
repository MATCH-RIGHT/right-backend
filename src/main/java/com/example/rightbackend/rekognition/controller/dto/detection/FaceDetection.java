package com.example.rightbackend.rekognition.controller.dto.detection;

import software.amazon.awssdk.services.rekognition.model.DetectFacesResponse;

import java.util.List;

public record FaceDetection (
        List<FaceDetail> faceDetails,
        String orientationCorrection,
        MetaData responseMetadata) {
    public static FaceDetection from(DetectFacesResponse response) {
        List<FaceDetail> faceDetails = response.faceDetails().stream()
                .map(FaceDetail::from)
                .toList();

        MetaData metaData = new MetaData(
                response.responseMetadata().requestId(),
                null, null, null
        );

        return new FaceDetection(
                faceDetails,
                response.orientationCorrectionAsString(),
                metaData
        );
    }
}