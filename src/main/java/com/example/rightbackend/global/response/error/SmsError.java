package com.example.rightbackend.global.response.error;

import org.springframework.http.HttpStatus;

public enum SmsError implements ErrorCode {
    INVALID_CERTIFICATION_CODE(HttpStatus.BAD_REQUEST, "S1", "올바르지 않은 인증번호입니다")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    SmsError(final HttpStatus httpStatus, final String code, final String message) {
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