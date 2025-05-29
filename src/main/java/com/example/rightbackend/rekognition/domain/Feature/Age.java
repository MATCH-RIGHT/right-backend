package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum Age {
    YOUNG("동안"),
    ADULT("어른스러운");

    private final String name;

    Age(String name) {
        this.name = name;
    }
}