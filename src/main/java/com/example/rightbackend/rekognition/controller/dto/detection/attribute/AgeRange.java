package com.example.rightbackend.rekognition.controller.dto.detection.attribute;

public record AgeRange(
        int low,
        int high) {
    public static AgeRange from(software.amazon.awssdk.services.rekognition.model.AgeRange ageRange) {
        return new AgeRange(ageRange.low(), ageRange.high());
    }
}