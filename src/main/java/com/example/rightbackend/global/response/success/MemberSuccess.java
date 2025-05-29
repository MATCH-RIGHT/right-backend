package com.example.rightbackend.global.response.success;

import org.springframework.http.HttpStatus;

public enum MemberSuccess implements SuccessCode {
    SIGN_UP_SUCCESS(HttpStatus.OK, "M001", "회원가입이 완료되었습니다"),
    CHECK_ID_SUCCESS(HttpStatus.OK, "M002", "사용 할 수 있는 아이디입니다"),
    GET_MEMBER_PAGE_SUCCESS(HttpStatus.OK, "M003", "회원 페이지 조회를 완료했습니다"),
    MEMBER_PAGE_CHANGE_SUCCESS(HttpStatus.OK, "M004", "회원 페이지 변경이 완료했습니다"),
    SEARCH_ID_SUCCESS(HttpStatus.OK, "M005", "아이디 조회를 완료했습니다"),
    CHANGE_PASSWORD_SUCCESS(HttpStatus.OK, "M006", "비밀번호 완료했습니다"),
    GET_INTERESTS_SUCCESS(HttpStatus.OK, "M007", "관심사 목록 조회를 완료했습니다"),
    GET_LOCATIONS_SUCCESS(HttpStatus.OK, "M008", "지역 목록 조회를 완료했습니다"),
    UPDATE_PROFILE_SUCCESS(HttpStatus.OK, "M009", "회원 정보 수정을 완료했습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MemberSuccess(final HttpStatus httpStatus, final String code, final String message) {
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