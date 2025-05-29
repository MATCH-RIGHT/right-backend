package com.example.rightbackend.sms.controller;

import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.SmsSuccess;
import com.example.rightbackend.sms.controller.dto.request.SmsConfirmRequest;
import com.example.rightbackend.sms.controller.dto.request.SmsSendRequest;
import com.example.rightbackend.sms.service.SmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    private final SmsService smsService;

    public SmsController(final SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public ResponseEntity<SuccessResponse<String>> sendSms(@RequestBody SmsSendRequest request) {
        return SuccessResponse.of(SmsSuccess.VERIFICATION_CODE_SEND_SUCCESS, smsService.sendSms(request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<SuccessResponse<String>> SmsVerification(@RequestBody SmsConfirmRequest request) {
        return SuccessResponse.of(SmsSuccess.EQUAL_VERIFICATION_CODE_SUCCESS, smsService.verifySms(request));
    }
}