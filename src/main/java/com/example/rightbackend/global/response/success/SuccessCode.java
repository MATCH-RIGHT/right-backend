package com.example.rightbackend.global.response.success;

import org.springframework.http.HttpStatus;

public interface SuccessCode {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}