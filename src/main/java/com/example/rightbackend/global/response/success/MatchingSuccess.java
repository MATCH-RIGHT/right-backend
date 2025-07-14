package com.example.rightbackend.global.response.success;

import org.springframework.http.HttpStatus;

public enum MatchingSuccess implements SuccessCode {

    GET_ACTIVE_MATCHINGS_SUCCESS(HttpStatus.OK, "M001", "활성화된 매칭 목록 조회에 성공했습니다."),
    GET_MATCHED_RESULTS_SUCCESS(HttpStatus.OK, "M002", "매칭된 결과 목록 조회에 성공했습니다."),
    EXECUTE_FREE_MATCHING_SUCCESS(HttpStatus.OK, "M003", "무료 매칭이 성공적으로 실행되었습니다."),
    EXECUTE_PREMIUM_MATCHING_SUCCESS(HttpStatus.OK, "M004", "유료 매칭이 성공적으로 실행되었습니다."),
    LIKE_MATCHING_SUCCESS(HttpStatus.OK, "M005", "매칭에 좋아요를 보냈습니다."),
    GET_PERMANENT_MATCHES_SUCCESS(HttpStatus.OK, "M006" , "성공된 매칭 결과 목록 조회에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MatchingSuccess(HttpStatus httpStatus, String code, String message) {
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