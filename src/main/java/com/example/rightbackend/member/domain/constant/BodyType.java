package com.example.rightbackend.member.domain.constant;

public enum BodyType {
    SKINNY(1, "마른"),
    SLIM(2, "슬림"),
    NORMAL(3, "보통"),
    MUSCULAR(4, "근육질"),
    CHUBBY(5, "통통");

    private final int id;
    private final String label;

    BodyType(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static BodyType fromId(int id) {
        for (BodyType bodyType : values()) {
            if (bodyType.id == id) {
                return bodyType;
            }
        }
        throw new IllegalArgumentException("Invalid body type id: " + id);
    }

    public static BodyType fromName(String name) {
        try {
            return BodyType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid body type name: " + name);
        }
    }
}