package com.example.rightbackend.rekognition.controller.dto.detection.attribute;


public record BoundingBox(
        float width,
        float height,
        float left,
        float top) {
    public static BoundingBox from(software.amazon.awssdk.services.rekognition.model.BoundingBox box) {
        return new BoundingBox(box.width(), box.height(), box.left(), box.top());
    }
}