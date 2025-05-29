package com.example.rightbackend.rekognition.controller.dto.detection.attribute;

public record EyeDirection (
        Float yaw,
        Float pitch,
        Float confidence){
    public static EyeDirection from(software.amazon.awssdk.services.rekognition.model.EyeDirection eyeDirection) {
        return new EyeDirection(
                eyeDirection.yaw(),
                eyeDirection.pitch(),
                eyeDirection.confidence()
        );
    }
}