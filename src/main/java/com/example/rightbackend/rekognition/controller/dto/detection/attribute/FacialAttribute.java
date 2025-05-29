package com.example.rightbackend.rekognition.controller.dto.detection.attribute;

public record FacialAttribute(
        boolean value,
        float confidence) {

    public static FacialAttribute from(software.amazon.awssdk.services.rekognition.model.Smile attribute) {
        return new FacialAttribute(attribute.value(), attribute.confidence());
    }

    public static FacialAttribute from(software.amazon.awssdk.services.rekognition.model.Eyeglasses attribute) {
        return new FacialAttribute(attribute.value(), attribute.confidence());
    }

    public static FacialAttribute from(software.amazon.awssdk.services.rekognition.model.Sunglasses attribute) {
        return new FacialAttribute(attribute.value(), attribute.confidence());
    }

    public static FacialAttribute from(software.amazon.awssdk.services.rekognition.model.Beard attribute) {
        return new FacialAttribute(attribute.value(), attribute.confidence());
    }

    public static FacialAttribute from(software.amazon.awssdk.services.rekognition.model.Mustache attribute) {
        return new FacialAttribute(attribute.value(), attribute.confidence());
    }

    public static FacialAttribute from(software.amazon.awssdk.services.rekognition.model.EyeOpen attribute) {
        return new FacialAttribute(attribute.value(), attribute.confidence());
    }

    public static FacialAttribute from(software.amazon.awssdk.services.rekognition.model.MouthOpen attribute) {
        return new FacialAttribute(attribute.value(), attribute.confidence());
    }

    public static FacialAttribute from(software.amazon.awssdk.services.rekognition.model.FaceOccluded attribute) {
        return new FacialAttribute(attribute.value(), attribute.confidence());
    }

}