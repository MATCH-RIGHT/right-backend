package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum Glass {
    GLASSED(1, "안경을 쓴"),
    NOT(2, "안경을 쓰지 않은");

    private final int id;
    private final String name;

    Glass(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Glass fromId(int id) {
        for (Glass glass : values()) {
            if (glass.id == id) {
                return glass;
            }
        }
        throw new IllegalArgumentException("Invalid Glass id: " + id);
    }

    public static Glass fromName(String name) {
        for (Glass glass : values()) {
            if (glass.name.equals(name)) {
                return glass;
            }
        }
        throw new IllegalArgumentException("Invalid Glass name: " + name);
    }
}