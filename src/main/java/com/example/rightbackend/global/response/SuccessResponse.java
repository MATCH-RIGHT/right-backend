package com.example.rightbackend.global.response;

import com.example.rightbackend.global.response.success.SuccessCode;
import org.springframework.http.ResponseEntity;

public class SuccessResponse<T> extends ResponseEntity {
    public SuccessResponse(final SuccessCode successCode, final T result) {
        super(new CustomResponseDTO<>(successCode.getCode(), result),
                successCode.getHttpStatus()
        );
    }

    public static <T> SuccessResponse<T> of(final SuccessCode successCode, final T result) {
        return new SuccessResponse<>(successCode, result);
    }

    public static SuccessResponse of(final SuccessCode successCode) {
        return new SuccessResponse(successCode, successCode.getMessage());
    }
}