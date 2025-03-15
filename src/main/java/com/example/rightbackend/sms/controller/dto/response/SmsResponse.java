package com.example.rightbackend.sms.controller.dto.response;

public enum SmsResponse {
    SEND_VERIFICATION_CODE_SUCCESS("본인 인증 문자 전송에 성공했습니다"),
    EQUAL_VERIFICATION_CODE_SUCCESS("본인 인증 번호가 일치합니다")
    ;

    private final String message;

    SmsResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}