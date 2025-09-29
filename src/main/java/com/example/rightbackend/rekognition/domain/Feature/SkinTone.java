package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum SkinTone {
    BRIGHT(1, "밝은 피부 색"),
    DARK(2, "어두운 피부 색");

    private final int id;
    private final String name;

    SkinTone(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static SkinTone fromId(int id) {
        for (SkinTone tone : values()) {
            if (tone.id == id) {
                return tone;
            }
        }
        throw new IllegalArgumentException("Invalid SkinTone id: " + id);
    }

    public static SkinTone fromName(String name) {
        for (SkinTone tone : values()) {
            if (tone.name.equals(name)) {
                return tone;
            }
        }
        throw new IllegalArgumentException("Invalid SkinTone name: " + name);
    }
}