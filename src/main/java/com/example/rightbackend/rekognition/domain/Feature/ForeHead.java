package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum ForeHead {
    NARROW(1, "좁은 이마"),
    BROAD(2, "넓은 이마");

    private final int id;
    private final String name;

    ForeHead(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ForeHead fromId(int id) {
        for (ForeHead foreHead : values()) {
            if (foreHead.id == id) {
                return foreHead;
            }
        }
        throw new IllegalArgumentException("Invalid ForeHead id: " + id);
    }

    public static ForeHead fromName(String name) {
        for (ForeHead foreHead : values()) {
            if (foreHead.name.equals(name)) {
                return foreHead;
            }
        }
        throw new IllegalArgumentException("Invalid ForeHead name: " + name);
    }
}