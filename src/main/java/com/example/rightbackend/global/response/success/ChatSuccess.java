package com.example.rightbackend.global.response.success;

import org.springframework.http.HttpStatus;

public enum ChatSuccess implements SuccessCode {
    SEND_MESSAGE_SUCCESS(HttpStatus.OK, "C001", "메시지 전송에 성공했습니다."),
    READ_MESSAGES_SUCCESS(HttpStatus.OK, "C002", "메시지 읽음 처리에 성공했습니다."),
    GET_SUMMARIES_SUCCESS(HttpStatus.OK,"C003", "채팅 요약 정보 조회에 성공했습니다."),
    GET_MESSAGES_SUCCESS(HttpStatus.OK,"C004", "메시지 조회에 성공했습니다."),
    CREATE_CHAT_ROOM_SUCCESS(HttpStatus.OK,"C005", "채팅방 생성에 성공했습니다."),
    REPORT_SUCCESS(HttpStatus.OK,"C006", "신고가 접수되었습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ChatSuccess(HttpStatus httpStatus, String code, String message) {
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