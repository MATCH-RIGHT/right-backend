package com.example.rightbackend.global.response.error;

import org.springframework.http.HttpStatus;

public enum FCMError implements ErrorCode {
    FCM_CONFIGURATION_ERROR(HttpStatus.NOT_FOUND, "F1", "FCM 설정 실패"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    FCMError(final HttpStatus httpStatus, final String code, final String message) {
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