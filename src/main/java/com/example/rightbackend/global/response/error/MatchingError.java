package com.example.rightbackend.global.response.error;

import org.springframework.http.HttpStatus;

public enum MatchingError implements ErrorCode {

    UNSUPPORTED_MATCHING_TYPE(HttpStatus.BAD_REQUEST, "M001", "지원하지 않는 매칭 타입입니다."),
    MATCHING_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "M002", "존재하지 않는 매칭 결과입니다."),
    EXPIRED_MATCHING(HttpStatus.BAD_REQUEST, "M003", "만료된 매칭입니다."),
    UNAUTHORIZED_USER(HttpStatus.FORBIDDEN, "M004", "해당 매칭에 참여하지 않은 사용자입니다."),
    MATCHING_EXECUTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "M005", "매칭 실행 중 오류가 발생했습니다."),
    DUPLICATE_LIKE_REQUEST(HttpStatus.BAD_REQUEST, "M006", "이미 좋아요를 누른 매칭입니다."),
    INSUFFICIENT_MEMBERS(HttpStatus.BAD_REQUEST, "M007", "매칭 가능한 회원이 부족합니다."),
    MATCHING_CLEANUP_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "M008", "매칭 정리 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MatchingError(final HttpStatus httpStatus, final String code, final String message) {
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
