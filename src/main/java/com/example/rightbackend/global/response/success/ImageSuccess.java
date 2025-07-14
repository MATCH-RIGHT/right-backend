package com.example.rightbackend.global.response.success;

import org.springframework.http.HttpStatus;

public enum ImageSuccess implements SuccessCode {

    IMAGE_UPLOAD_SUCCESS(HttpStatus.OK, "I001", "이미지 업로드를 완료했습니다"),
    IMAGE_DELETE_SUCCESS(HttpStatus.OK, "I002", "이미지 삭제를 완료했습니다"),
    IMAGE_CHANGE_SUCCESS(HttpStatus.OK, "I003", "이미지 변경을 완료했습니다"),
    IMAGE_GET_SUCCESS(HttpStatus.OK, "I004", "이미지 목록 조회를 완료했습니다"),
    ALL_FACE_FEATURES_GET_SUCCESS(HttpStatus.OK, "I005", "얼굴 특징 목록 조회 성공"),
    MY_FEATURE_UPLOAD_SUCCESS(HttpStatus.OK, "I006", "얼굴 특징 업로드를 완료했습니다"),
    MY_FEATURE_GET_SUCCESS(HttpStatus.OK, "I007", "나의 얼굴 특징 조회를 완료했습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ImageSuccess(final HttpStatus httpStatus, final String code, final String message) {
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