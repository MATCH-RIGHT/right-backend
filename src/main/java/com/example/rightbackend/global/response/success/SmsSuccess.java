package com.example.rightbackend.global.response.success;

import org.springframework.http.HttpStatus;

public enum SmsSuccess implements SuccessCode {
    VERIFICATION_CODE_SEND_SUCCESS(HttpStatus.OK, "S001", "본인 인증 문자 전송에 성공했습니다"),
    EQUAL_VERIFICATION_CODE_SUCCESS(HttpStatus.OK, "S002", "본인 인증 코드 검사에 성공했습니다")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    SmsSuccess(final HttpStatus httpStatus, final String code, final String message) {
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