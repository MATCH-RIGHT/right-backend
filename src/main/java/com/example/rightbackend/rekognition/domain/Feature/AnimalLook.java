package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum AnimalLook {
    CAT("고양이상"), DOG("강아지상"), BEAR("곰돌이상"), RABBIT("토끼상"),
    FOX("여우상"), QUOKKA("쿼카상"), DINO("공룡상"), HORSE("말상");

    private final String name;

    AnimalLook(String name) {
        this.name = name;
    }
}