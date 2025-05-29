package com.example.rightbackend.global.response.error;

import org.springframework.http.HttpStatus;

public enum MemberError implements ErrorCode {
    INVALID_ID(HttpStatus.BAD_REQUEST, "M2", "올바르지 않은 형식의 아이디"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "M1", "올바르지 않은 형식의 비밀번호"),
    NULL_MEMBER(HttpStatus.UNPROCESSABLE_ENTITY, "M3", "존재하지 않는 회원"),
    INCORRECT_PASSWORD(HttpStatus.SERVICE_UNAVAILABLE, "M4", "비밀번호가 옳지 않음"),
    WITHDRAW_MEMBER(HttpStatus.BAD_REQUEST,"M5" ,"탈퇴한 회원에 접근" ),
    DUPLICATE_ID(HttpStatus.CONFLICT, "M6","이미 존재하는 회원"),
    NULL_LOCATION(HttpStatus.CONFLICT, "M7","존재하지 않는 지역"),
    INVALID_FEATURE_INDEX(HttpStatus.BAD_REQUEST, "M8", "특징 인덱스는 0 이상이어야 합니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MemberError(final HttpStatus httpStatus, final String code, final String message) {
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