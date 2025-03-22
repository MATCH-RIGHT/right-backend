package com.example.rightbackend.uploader.controller.dto.response;

public enum UploadResponse {
    SINGLE_UPLOAD_SUCCESS("단일 이미지 업로드를 완료했습니다"),
    MULTI_UPLOAD_SUCCESS("멀티 이미지 업로드를 완료했습니다");

    private final String message;

    UploadResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}