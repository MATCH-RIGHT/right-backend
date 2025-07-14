package com.example.rightbackend.sms.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SmsConfirmRequest(
    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^01[0-9]-[0-9]{4}-[0-9]{4}$", message = "전화번호 형식이 올바르지 않습니다")
    String phoneNumber,
    
    @NotBlank(message = "인증번호는 필수입니다")
    @Pattern(regexp = "^[0-9]{6}$", message = "인증번호는 6자리 숫자여야 합니다")
    String certificationNumber
){
}