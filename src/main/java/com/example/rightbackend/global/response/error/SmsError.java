package com.example.rightbackend.global.response.error;

import org.springframework.http.HttpStatus;

public enum SmsError implements ErrorCode {
    
    INVALID_CERTIFICATION_CODE(HttpStatus.BAD_REQUEST, "S1", "올바르지 않은 인증번호입니다"),
    SMS_SEND_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "S2", "SMS 전송에 실패했습니다"),
    CERTIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "S3", "인증번호가 만료되었습니다"),
    REDIS_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S4", "인증번호 저장소 연결에 실패했습니다"),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "S5", "올바르지 않은 전화번호 형식입니다")
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