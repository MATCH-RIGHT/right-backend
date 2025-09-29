package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum Age {
    YOUNG(1, "동안"),
    ADULT(2, "어른스러운");

    private final int id;
    private final String name;

    Age(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Age fromId(int id) {
        for (Age age : values()) {
            if (age.id == id) {
                return age;
            }
        }
        throw new IllegalArgumentException("Invalid Age id: " + id);
    }

    public static Age fromName(String name) {
        for (Age age : values()) {
            if (age.name.equals(name)) {
                return age;
            }
        }
        throw new IllegalArgumentException("Invalid Age name: " + name);
    }
}