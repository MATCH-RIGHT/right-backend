package com.example.rightbackend.global.response.error;

import org.springframework.http.HttpStatus;

public enum MemberError implements ErrorCode {

    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "M1", "비밀번호가 일치하지 않습니다"),
    INVALID_ID(HttpStatus.BAD_REQUEST, "M2", "아이디를 입력해주세요"),
    NULL_MEMBER(HttpStatus.UNPROCESSABLE_ENTITY, "M3", "존재하지 않는 회원입니다"),
    INCORRECT_PASSWORD(HttpStatus.SERVICE_UNAVAILABLE, "M4", "비밀번호가 옳지 않음"),
    EMPTY_PASSWORD(HttpStatus.BAD_REQUEST, "M9", "비밀번호를 입력해주세요"),
    WITHDRAW_MEMBER(HttpStatus.BAD_REQUEST,"M5" ,"탈퇴한 회원에 접근" ),
    DUPLICATE_ID(HttpStatus.CONFLICT, "M6","이미 존재하는 회원"),
    NULL_LOCATION(HttpStatus.CONFLICT, "M7","존재하지 않는 지역"),
    INVALID_FEATURE_INDEX(HttpStatus.BAD_REQUEST, "M8", "특징 인덱스는 0 이상이어야 합니다."),
    INVALID_PHONE_FORMAT(HttpStatus.BAD_REQUEST, "M10", "올바른 전화번호 형식을 입력해주세요"),
    WEAK_PASSWORD(HttpStatus.BAD_REQUEST, "M11", "비밀번호는 8자 이상 24자 이하, 영문/숫자/특수문자를 포함해야 합니다"),
    INVALID_NAME_FORMAT(HttpStatus.BAD_REQUEST, "M12", "이름을 입력해주세요"),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "M13", "닉네임을 입력해주세요"),
    INVALID_PROVIDER_ID_FORMAT(HttpStatus.BAD_REQUEST, "M14", "아이디는 3-20자의 영문/숫자만 입력 가능합니다"),
    INVALID_HEIGHT_FORMAT(HttpStatus.BAD_REQUEST, "M15", "신장은 120-250cm 범위로 입력해주세요"),
    INVALID_BIRTHDAY_FORMAT(HttpStatus.BAD_REQUEST, "M16", "생년월일을 올바른 형식(YYYY-MM-DD)으로 입력해주세요"),
    INVALID_GENDER(HttpStatus.BAD_REQUEST, "M17", "유효하지 않은 성별입니다"),
    INVALID_BODY_TYPE(HttpStatus.BAD_REQUEST, "M18", "유효하지 않은 체형입니다"),
    INVALID_JOB(HttpStatus.BAD_REQUEST, "M19", "유효하지 않은 직업입니다"),
    INVALID_LOCATION_ID(HttpStatus.BAD_REQUEST, "M20", "유효하지 않은 지역 ID입니다"),
    INVALID_INTEREST_ID(HttpStatus.BAD_REQUEST, "M21", "유효하지 않은 관심사 ID입니다");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MemberError(final HttpStatus httpStatus, final String code, final String message) {
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