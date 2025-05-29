package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum NoseShape {
    SMART("오똑한"),
    BALANCE("균형잡힌"),
    LOW("낮은");

    private final String name;

    NoseShape(String name) {
        this.name = name;
    }
}