package com.example.rightbackend.rekognition.controller.dto.detection;

import com.example.rightbackend.rekognition.controller.dto.detection.attribute.*;

import java.util.List;

public record FaceDetail (
    BoundingBox boundingBox,
    AgeRange ageRange,
    FacialAttribute smile,
    FacialAttribute eyeglasses,
    FacialAttribute sunglasses,
    Gender gender,
    FacialAttribute beard,
    FacialAttribute mustache,
    FacialAttribute eyesOpen,
    FacialAttribute mouthOpen,
    List<Emotion> emotions,
    List<Landmark> landmarks,
    Pose pose,
    Quality quality,
    double confidence,
    FacialAttribute faceOccluded,
    EyeDirection eyeDirection) {

    public static FaceDetail from(software.amazon.awssdk.services.rekognition.model.FaceDetail detail) {
        return new FaceDetail(
                detail.boundingBox() != null ? BoundingBox.from(detail.boundingBox()) : null,
                detail.ageRange() != null ? AgeRange.from(detail.ageRange()) : null,
                detail.smile() != null ? FacialAttribute.from(detail.smile()) : null,
                detail.eyeglasses() != null ? FacialAttribute.from(detail.eyeglasses()) : null,
                detail.sunglasses() != null ? FacialAttribute.from(detail.sunglasses()) : null,
                detail.gender() != null ? Gender.from(detail.gender()) : null,
                detail.beard() != null ? FacialAttribute.from(detail.beard()) : null,
                detail.mustache() != null ? FacialAttribute.from(detail.mustache()) : null,
                detail.eyesOpen() != null ? FacialAttribute.from(detail.eyesOpen()) : null,
                detail.mouthOpen() != null ? FacialAttribute.from(detail.mouthOpen()) : null,
                detail.emotions() != null ? detail.emotions().stream().map(Emotion::from).toList() : null,
                detail.landmarks() != null ? detail.landmarks().stream().map(Landmark::from).toList() : null,
                detail.pose() != null ? Pose.from(detail.pose()) : null,
                detail.quality() != null ? Quality.from(detail.quality()) : null,
                detail.confidence(),
                detail.faceOccluded() != null ? FacialAttribute.from(detail.faceOccluded()) : null,
                detail.eyeDirection() != null ? EyeDirection.from(detail.eyeDirection()) : null
        );
    }
}