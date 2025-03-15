package com.example.rightbackend.global.exception;

import com.example.rightbackend.global.response.error.ErrorCode;
import lombok.Getter;

@Getter
public class RestApiException extends RuntimeException {

    private final ErrorCode errorCode;

    public RestApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}