package com.example.rightbackend.global.response.success;

import org.springframework.http.HttpStatus;

public enum NotificationSuccess implements SuccessCode {
    
    GET_NOTIFICATIONS_SUCCESS(HttpStatus.OK, "N001", "알림 목록을 성공적으로 조회했습니다."),
    READ_NOTIFICATION_SUCCESS(HttpStatus.OK, "N002", "알림을 성공적으로 읽음 처리했습니다."),
    COUNT_UNREAD_NOTIFICATIONS_SUCCESS(HttpStatus.OK, "N003", "읽지 않은 알림 개수를 성공적으로 조회했습니다."),
    REGISTER_FCM_TOKEN_SUCCESS(HttpStatus.OK, "N004", "FCM 토큰을 성공적으로 등록했습니다."),
    GET_FCM_STATUS_SUCCESS(HttpStatus.OK, "N005", "FCM 상태를 성공적으로 조회했습니다."),
    CHANGE_FCM_STATUS_SUCCESS(HttpStatus.OK, "N006", "FCM 상태를 성공적으로 변경했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


    NotificationSuccess(HttpStatus httpStatus, String code, String message) {
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