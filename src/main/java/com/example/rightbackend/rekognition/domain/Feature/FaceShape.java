package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum FaceShape {
    LONG(1, "긴 얼굴형"),
    OVAL(2, "계란형"),
    ROUND(3, "둥근형"),
    TRIANGLE(4, "역삼각형"),
    ANGULAR(5, "각진형");

    private final int id;
    private final String name;

    FaceShape(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static FaceShape fromId(int id) {
        for (FaceShape shape : values()) {
            if (shape.id == id) {
                return shape;
            }
        }
        throw new IllegalArgumentException("Invalid FaceShape id: " + id);
    }

    public static FaceShape fromName(String name) {
        for (FaceShape shape : values()) {
            if (shape.name.equals(name)) {
                return shape;
            }
        }
        throw new IllegalArgumentException("Invalid FaceShape name: " + name);
    }
}