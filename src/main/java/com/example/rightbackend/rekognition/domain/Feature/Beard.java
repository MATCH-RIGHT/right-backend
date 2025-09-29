package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum Beard {
    BEARDED(1, "수염 있는"),
    NOT(2, "수염 없는");

    private final int id;
    private final String name;

    Beard(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Beard fromId(int id) {
        for (Beard beard : values()) {
            if (beard.id == id) {
                return beard;
            }
        }
        throw new IllegalArgumentException("Invalid Beard id: " + id);
    }

    public static Beard fromName(String name) {
        for (Beard beard : values()) {
            if (beard.name.equals(name)) {
                return beard;
            }
        }
        throw new IllegalArgumentException("Invalid Beard name: " + name);
    }
}