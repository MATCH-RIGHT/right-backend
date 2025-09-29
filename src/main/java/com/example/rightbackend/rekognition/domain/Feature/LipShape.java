package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum LipShape {
    THICK(1, "두꺼운 입술"),
    THIN(2, "얇은 입술");

    private final int id;
    private final String name;

    LipShape(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static LipShape fromId(int id) {
        for (LipShape shape : values()) {
            if (shape.id == id) {
                return shape;
            }
        }
        throw new IllegalArgumentException("Invalid LipShape id: " + id);
    }

    public static LipShape fromName(String name) {
        for (LipShape shape : values()) {
            if (shape.name.equals(name)) {
                return shape;
            }
        }
        throw new IllegalArgumentException("Invalid LipShape name: " + name);
    }
}