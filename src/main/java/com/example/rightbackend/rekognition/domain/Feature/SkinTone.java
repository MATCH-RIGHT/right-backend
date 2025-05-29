package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum SkinTone {
    BRIGHT("밝은 피부 색"),
    DARK("어두운 피부 색");

    private final String name;

    SkinTone(String name) {
        this.name = name;
    }
}