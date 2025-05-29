package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum EyeType {
    UPTURNED("눈꼬리가 올라간 눈"),
    DOWNTURNED("눈꼬리가 내려간 눈"),
    ROUND("동그란 눈"),
    ALMOND("찢어진 눈");

    private final String name;

    EyeType(String name) {
        this.name = name;
    }
}