package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum LipShape {
    THICK("두꺼운 입술"),
    THIN("얇은 입술");

    private final String name;

    LipShape(String name) {
        this.name = name;
    }
}