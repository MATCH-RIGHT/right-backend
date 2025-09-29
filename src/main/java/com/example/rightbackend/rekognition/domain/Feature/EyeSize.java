package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum EyeSize {
    SMALL(1, "작은"),
    BIG(2, "큰");

    private final int id;
    private final String name;

    EyeSize(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static EyeSize fromId(int id) {
        for (EyeSize size : values()) {
            if (size.id == id) {
                return size;
            }
        }
        throw new IllegalArgumentException("Invalid EyeSize id: " + id);
    }

    public static EyeSize fromName(String name) {
        for (EyeSize size : values()) {
            if (size.name.equals(name)) {
                return size;
            }
        }
        throw new IllegalArgumentException("Invalid EyeSize name: " + name);
    }
}