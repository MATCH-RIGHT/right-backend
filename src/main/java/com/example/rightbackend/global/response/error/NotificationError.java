package com.example.rightbackend.global.response.error;

import org.springframework.http.HttpStatus;

public enum NotificationError implements ErrorCode {
    
    IS_NOT_OWNER(HttpStatus.FORBIDDEN, "N001", "알림의 소유자가 아닙니다."),
    NULL_NOTIFICATION(HttpStatus.NOT_FOUND, "N002", "존재하지 않는 알림입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    NotificationError(HttpStatus httpStatus, String code, String message) {
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