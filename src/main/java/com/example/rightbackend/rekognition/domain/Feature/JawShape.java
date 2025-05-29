package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum JawShape {
    SHARP("날카로운 턱선"),
    ROUND("둥근 턱선"),
    SQUARE("각진 턱선");

    private final String name;

    JawShape(String name) {
        this.name = name;
    }
}