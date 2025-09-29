package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum EyeType {
    UPTURNED(1, "눈꼬리가 올라간 눈"),
    DOWNTURNED(2, "눈꼬리가 내려간 눈"),
    ROUND(3, "동그란 눈"),
    ALMOND(4, "찢어진 눈");

    private final int id;
    private final String name;

    EyeType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static EyeType fromId(int id) {
        for (EyeType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid EyeType id: " + id);
    }

    public static EyeType fromName(String name) {
        for (EyeType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid EyeType name: " + name);
    }
}