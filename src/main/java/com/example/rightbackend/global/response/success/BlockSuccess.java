package com.example.rightbackend.global.response.success;

import org.springframework.http.HttpStatus;

public enum BlockSuccess implements SuccessCode {
    
    BLOCK_SUCCESS(HttpStatus.OK,"B001", "차단이 완료되었습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    BlockSuccess(HttpStatus httpStatus, String code, String message) {
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