package com.example.rightbackend.member.controller.dto.response;

public enum MemberResponse {
    ALREADY_SIGN_UP("이미 가입 된 회원입니다"),
    AVAILABLE_ID("사용 가능 한 아이디입니다"),
    SIGN_UP_SUCCESS("회원가입이 완료되었습니다"),
    LOGOUT_SUCCESS("로그아웃 되었습니다"),
    WITHDRAW_SUCCESS("회원이 탈퇴되었습니다"),
    PASSWORD_CHANGE_SUCCESS("비밀번호 변경이 완료되었습니다"),
    IMAGE_CHANGE_SUCCESS("프로필 이미지 변경이 완료되었습니다"),
    USERINFO_CHANGE_SUCCESS("회원정보 변경이 완료되었습니다");

    private final String message;

    MemberResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}