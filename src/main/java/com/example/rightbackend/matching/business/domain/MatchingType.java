package com.example.rightbackend.matching.business.domain;

public enum MatchingType {
    FREE("FREE"),
    PREMIUM("PREMIUM");

    private final String value;

    MatchingType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
