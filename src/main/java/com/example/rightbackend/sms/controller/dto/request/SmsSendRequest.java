package com.example.rightbackend.sms.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SmsSendRequest(
    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^01[0-9]-[0-9]{4}-[0-9]{4}$", message = "전화번호 형식이 올바르지 않습니다 (예: 010-1234-5678)")
    String phoneNumber
) {
}