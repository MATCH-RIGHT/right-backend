package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum EyeSize {
    SMALL("작은"),
    BIG("큰");

    private final String name;

    EyeSize(String name) {
        this.name = name;
    }
}