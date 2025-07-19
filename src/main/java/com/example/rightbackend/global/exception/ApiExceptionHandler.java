package com.example.rightbackend.global.exception;

import com.example.rightbackend.global.response.error.ErrorCode;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Order(value = Integer.MIN_VALUE)
public class ApiExceptionHandler {
    @ExceptionHandler(value = RestApiException.class)
    public ResponseEntity<Object> apiException(RestApiException apiException) {
        ErrorCode errorCode = apiException.getErrorCode();
        Map<String, String> errorResponse = new LinkedHashMap<>();
        errorResponse.put("code", errorCode.getCode());
        errorResponse.put("message", errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }
}