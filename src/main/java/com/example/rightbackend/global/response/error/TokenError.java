package com.example.rightbackend.global.response.error;

import org.springframework.http.HttpStatus;

public enum TokenError implements ErrorCode {

    INVALID_TOKEN(HttpStatus.BAD_REQUEST,"T1", "올바르지 않은 AccessToken 입니다"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED,"T2", "만료 된 AccessToken 입니다"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "T3", "만료 된 RefreshToken 입니다"),
    NULL_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"T4", "존재하지 않은 RefreshToken 입니다"),
    NOT_ACCESS_TOKEN_FOR_REISSUE(HttpStatus.BAD_REQUEST,"T5","재발급하기에는 유효기간이 남은 AccessToken 입니다");;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    TokenError(final HttpStatus httpStatus, final String code, final String message) {
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