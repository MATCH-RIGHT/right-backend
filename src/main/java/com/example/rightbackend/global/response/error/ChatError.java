package com.example.rightbackend.global.response.error;

import org.springframework.http.HttpStatus;

public enum ChatError implements ErrorCode {
    
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C002", "채팅방에 접근할 권한이 없습니다."),
    CHAT_ROOM_ALREADY_BLOCKED(HttpStatus.BAD_REQUEST, "C003", "이미 차단된 채팅방입니다."),
    CHAT_ROOM_ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "C004", "이미 종료된 채팅방입니다."),
    CHAT_REPORT_CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "C005", "유효하지 않은 신고 카테고리입니다."),
    CHAT_ROOM_CLOSED(HttpStatus.BAD_REQUEST, "C006", "종료된 채팅방입니다."),
    CHAT_ROOM_BLOCKED(HttpStatus.BAD_REQUEST, "C007", "차단된 채팅  방입니다."),
    CHAT_ROOM_WAITING(HttpStatus.BAD_REQUEST, "C008", "대기 중인 채팅방입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "C009", "회원을 찾을 수 없습니다."),
    INVALID_REPORT_CATEGORY(HttpStatus.BAD_REQUEST, "C010", "유효하지 않은 신고 카테고리입니다."),
    MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C011", "메시지 전송에 실패했습니다."),
    MESSAGE_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C012", "메시지 읽기 처리에 실패했습니다."),
    INVALID_MESSAGE_CONTENT(HttpStatus.BAD_REQUEST, "C013", "메시지 내용이 유효하지 않습니다."),
    INVALID_ROOM_ID(HttpStatus.BAD_REQUEST, "C014", "유효하지 않은 채팅방 ID입니다."),
    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST, "C015", "유효하지 않은 회원 ID입니다."),
    MESSAGE_TOO_LONG(HttpStatus.BAD_REQUEST, "C016", "메시지가 너무 깁니다."),
    WEBSOCKET_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C017", "웹소켓 연결 오류가 발생했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ChatError(HttpStatus httpStatus, String code, String message) {
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