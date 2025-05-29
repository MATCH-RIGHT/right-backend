package com.example.rightbackend.rekognition.controller.dto.detection.attribute;

public record Emotion(
        String type,
        Float confidence) {
    public static Emotion from(software.amazon.awssdk.services.rekognition.model.Emotion emotion) {
        return new Emotion(
                emotion.typeAsString(),
                emotion.confidence()
        );
    }
}