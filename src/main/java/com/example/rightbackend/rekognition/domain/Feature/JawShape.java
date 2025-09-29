package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum JawShape {
    SHARP(1, "날카로운 턱선"),
    ROUND(2, "둥근 턱선"),
    SQUARE(3, "각진 턱선");

    private final int id;
    private final String name;

    JawShape(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static JawShape fromId(int id) {
        for (JawShape shape : values()) {
            if (shape.id == id) {
                return shape;
            }
        }
        throw new IllegalArgumentException("Invalid JawShape id: " + id);
    }

    public static JawShape fromName(String name) {
        for (JawShape shape : values()) {
            if (shape.name.equals(name)) {
                return shape;
            }
        }
        throw new IllegalArgumentException("Invalid JawShape name: " + name);
    }
}