package com.example.rightbackend.rekognition.controller.dto.detection.attribute;

public record Pose (
        Float roll,
        Float yaw,
        Float pitch) {
    public static Pose from(software.amazon.awssdk.services.rekognition.model.Pose pose) {
        return new Pose(
                pose.roll(),
                pose.yaw(),
                pose.pitch()
        );
    }
}