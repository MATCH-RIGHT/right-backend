package com.example.rightbackend.global.response.error;

import org.springframework.http.HttpStatus;

public enum ImageError implements ErrorCode {
    
    IMAGE_FORMAT_ERROR(HttpStatus.BAD_REQUEST, "I001", "지원하지 않는 이미지 형식입니다. PNG, JPG, JPEG, GIF 파일만 업로드 가능합니다"),
    LIMIT_IMAGE_SIZE_ERROR(HttpStatus.BAD_REQUEST, "I002", "이미지 크기가 10MB를 초과했습니다"),
    IMAGE_IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "I003", "이미지 업로드 중 오류가 발생했습니다"),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "I004", "요청한 이미지를 찾을 수 없습니다"),
    IMAGE_NOT_DETECTION(HttpStatus.BAD_REQUEST, "I005", "얼굴 감지 중 오류가 발생했습니다"),
    EMPTY_FILE_LIST(HttpStatus.BAD_REQUEST, "I006", "업로드할 이미지를 선택해주세요"),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "I007", "빈 파일은 업로드할 수 없습니다"),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "I008", "올바르지 않은 파일명입니다"),
    LIMIT_IMAGE_COUNT_ERROR(HttpStatus.BAD_REQUEST, "I009", "한 번에 최대 5개의 이미지만 업로드 가능합니다"),
    UNAUTHORIZED_IMAGE_ACCESS(HttpStatus.FORBIDDEN, "I010", "다른 사용자의 이미지에 접근할 수 없습니다"),
    INVALID_IMAGE_ORDER(HttpStatus.BAD_REQUEST, "I011", "잘못된 이미지 순서 요청입니다")
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