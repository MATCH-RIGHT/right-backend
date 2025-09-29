package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum NoseShape {
    SMART(1, "오똑한"),
    BALANCE(2, "균형잡힌"),
    LOW(3, "낮은");

    private final int id;
    private final String name;

    NoseShape(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static NoseShape fromId(int id) {
        for (NoseShape shape : values()) {
            if (shape.id == id) {
                return shape;
            }
        }
        throw new IllegalArgumentException("Invalid NoseShape id: " + id);
    }

    public static NoseShape fromName(String name) {
        for (NoseShape shape : values()) {
            if (shape.name.equals(name)) {
                return shape;
            }
        }
        throw new IllegalArgumentException("Invalid NoseShape name: " + name);
    }
}