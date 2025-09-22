package com.example.rightbackend.member.domain.constant;

public enum Gender {
    MALE(1, "남성"),
    FEMALE(2, "여성");

    private final int id;
    private final String label;

    Gender(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static Gender fromId(int id) {
        for (Gender gender : values()) {
            if (gender.id == id) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Invalid gender id: " + id);
    }

    public static Gender fromName(String name) {
        try {
            return Gender.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid gender name: " + name);
        }
    }
}