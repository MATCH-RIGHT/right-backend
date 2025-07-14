package com.example.rightbackend.global.response.error;

import org.springframework.http.HttpStatus;

public enum MatchingFilterError implements ErrorCode {
    
    NULL_MEMBER_PROFILE(HttpStatus.NOT_FOUND, "MF001", "존재하지 않는 회원 프로필입니다."),
    INVALID_AGE_RANGE(HttpStatus.BAD_REQUEST, "MF002", "유효하지 않은 나이 범위입니다."),
    INVALID_FACE_FEATURE(HttpStatus.BAD_REQUEST, "MF003", "유효하지 않은 얼굴 특징입니다."),
    NULL_REGION(HttpStatus.NOT_FOUND, "MF004", "존재하지 않는 지역입니다."),
    NEGATIVE_FEATURE_INDEX(HttpStatus.BAD_REQUEST, "MF005", "특징 인덱스는 0 이상이어야 합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MatchingFilterError(final HttpStatus httpStatus, final String code, final String message) {
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