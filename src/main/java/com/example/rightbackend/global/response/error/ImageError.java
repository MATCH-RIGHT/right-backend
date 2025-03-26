package com.example.rightbackend.global.response.error;

import org.springframework.http.HttpStatus;

public enum ImageError implements ErrorCode {
    IMAGE_FORMAT_ERROR(HttpStatus.BAD_REQUEST, "I1", "올바르지 않은 이미지 저장입니다"),
    LIMIT_IMAGE_SIZE_ERROR(HttpStatus.BAD_REQUEST, "I2", "초과한 용량의 이미지 저장입니다"),
    IMAGE_IO_ERROR(HttpStatus.BAD_REQUEST, "I3", "존재하지 않거나 접근할 수 없는 이미지입니다"),
    IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "I4", "존재하지 않는 이미지입니다")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ImageError(final HttpStatus httpStatus, final String code, final String message) {
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