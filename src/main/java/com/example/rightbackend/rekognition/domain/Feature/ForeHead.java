package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum ForeHead {
    NARROW("좁은 이마"),
    BROAD("넓은 이마");

    private final String name;

    ForeHead(String name) {
        this.name = name;
    }
}