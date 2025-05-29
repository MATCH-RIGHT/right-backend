package com.example.rightbackend.image.controller.dto.response;

public enum ImageResponse {
    SINGLE_UPLOAD_SUCCESS("단일 이미지 업로드를 완료했습니다"),
    FEATURE_UPLOAD_SUCCESS("특징 추출을 위한 단일 이미지 업로드를 완료했습니다"),
    FACE_ANALYSIS_SUCCESS("얼굴 특징 분석을 완료했습니다"),
    MULTI_UPLOAD_SUCCESS("멀티 이미지 업로드를 완료했습니다"),
    IMAGE_CHANGE_SUCCESS("이미지 변경을 완료했습니다"),
    DELETE_SUCCESS("이미지 삭제를 완료했습니다");

    private final String message;

    ImageResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}