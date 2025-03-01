package com.example.rightbackend.global.response.success;

import org.springframework.http.HttpStatus;

public enum MemberSuccess implements SuccessCode {
    SING_UP_SUCCESS(HttpStatus.OK, "M001", "회원가입이 완료되었습니다."),
    WITH_DRAW_SUCCESS(HttpStatus.OK, "M002", "회원 탈퇴가 완료되었습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MemberSuccess(final HttpStatus httpStatus, final String code, final String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}