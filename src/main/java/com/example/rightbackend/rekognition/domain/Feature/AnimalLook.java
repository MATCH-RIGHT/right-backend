package com.example.rightbackend.rekognition.domain.Feature;

import lombok.Getter;

@Getter
public enum AnimalLook {
    CAT(1, "고양이상"),
    DOG(2, "강아지상"),
    BEAR(3, "곰돌이상"),
    RABBIT(4, "토끼상"),
    FOX(5, "여우상"),
    QUOKKA(6, "쿼카상"),
    DINO(7, "공룡상"),
    HORSE(8, "말상");

    private final int id;
    private final String name;

    AnimalLook(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static AnimalLook fromId(int id) {
        for (AnimalLook look : values()) {
            if (look.id == id) {
                return look;
            }
        }
        throw new IllegalArgumentException("Invalid AnimalLook id: " + id);
    }

    public static AnimalLook fromName(String name) {
        for (AnimalLook look : values()) {
            if (look.name.equals(name)) {
                return look;
            }
        }
        throw new IllegalArgumentException("Invalid AnimalLook name: " + name);
    }
}