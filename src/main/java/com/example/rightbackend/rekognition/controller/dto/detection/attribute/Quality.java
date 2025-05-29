package com.example.rightbackend.rekognition.controller.dto.detection.attribute;

public record Quality (
        Float brightness,
        Float sharpness){
    public static Quality from(software.amazon.awssdk.services.rekognition.model.ImageQuality quality) {
        return new Quality(
                quality.brightness(),
                quality.sharpness()
        );
    }
}