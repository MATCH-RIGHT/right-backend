package com.example.rightbackend.rekognition.controller.dto.detection.attribute;

public record Gender(
        String value,
        Float confidence) {
    public static Gender from(software.amazon.awssdk.services.rekognition.model.Gender gender) {
        return new Gender(
                gender.valueAsString(),
                gender.confidence()
        );
    }
}