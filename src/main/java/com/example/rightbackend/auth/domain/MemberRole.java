package com.example.rightbackend.auth.domain;

import lombok.Getter;

@Getter
public enum MemberRole {
    ADMIN(0, "관리자"),
    MEMBER(1, "회원");

    private final Integer code;
    private final String name;

    MemberRole(final Integer code, final String name) {
        this.code = code;
        this.name = name;
    }

    public static MemberRole of(final Integer code) {
        for (MemberRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No matching role for code: " + code);
    }
}