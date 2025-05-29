package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum Beard {
    BEARDED("수염 있는"),
    NOT("수염 없는");

    private final String name;

    Beard(String name) {
        this.name = name;
    }
}