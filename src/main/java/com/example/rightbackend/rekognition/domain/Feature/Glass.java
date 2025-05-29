package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum Glass {
    GLASSED("안경을 쓴"),
    NOT("안경을 쓰지 않은");

    private final String name;

    Glass(String name) {
        this.name = name;
    }
}