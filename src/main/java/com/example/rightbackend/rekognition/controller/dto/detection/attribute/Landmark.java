package com.example.rightbackend.rekognition.controller.dto.detection.attribute;

public record Landmark(
        String type,
        Float x,
        Float y) {
    public static Landmark from(software.amazon.awssdk.services.rekognition.model.Landmark landmark) {
        return new Landmark(
                landmark.typeAsString(),
                landmark.x(),
                landmark.y()
        );
    }
}