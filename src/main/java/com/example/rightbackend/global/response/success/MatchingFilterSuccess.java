package com.example.rightbackend.global.response.success;

import org.springframework.http.HttpStatus;

public enum MatchingFilterSuccess implements SuccessCode {

    GET_MATCHING_FILTER_SUCCESS(HttpStatus.OK, "MF001", "매칭 필터 조회에 성공했습니다."),
    UPDATE_MATCHING_FILTER_SUCCESS(HttpStatus.OK, "MF002", "매칭 필터 설정에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MatchingFilterSuccess(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
    
    @Override
    public String getCode() {
        return code;
    }
}