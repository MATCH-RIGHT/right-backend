package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum FaceShape {
    LONG("긴 얼굴형"),
    OVAL("계란형"),
    ROUND("둥근형"),
    TRIANGLE("역삼각형"),
    ANGULAR("각진형");

    private final String name;

    FaceShape(String name) {
        this.name = name;
    }
}