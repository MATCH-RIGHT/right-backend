package com.example.rightbackend.sms.service;

import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.SmsError;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class SmsUtils {
    @Value("${coolsms.api.senderNumber}")
    private String senderNumber;

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secretKey}")
    private String apiSecret;

    DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        try {
            if (!StringUtils.hasText(apiKey) || !StringUtils.hasText(apiSecret) || !StringUtils.hasText(senderNumber)) {
                throw new IllegalArgumentException("SMS API credentials are not properly configured");
            }
            this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
            log.info("SMS service initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize SMS service: {}", e.getMessage());
            throw new RestApiException(SmsError.SMS_SEND_FAILURE);
        }
    }

    public SingleMessageSentResponse sendSMS(String to, String verificationCode) {
        try {
            if (!StringUtils.hasText(to) || !StringUtils.hasText(verificationCode)) {
                throw new IllegalArgumentException("Phone number and verification code are required");
            }
            
            Message message = new Message();
            message.setFrom(senderNumber);
            message.setTo(to);
            message.setText("[라잇] 본인 확인 인증번호는 " + verificationCode + "입니다.");

            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
            
            if (response.getStatusCode() == null || !response.getStatusCode().equals("2000")) {
                log.error("SMS send failed with status code: {}", response.getStatusCode());
                throw new RestApiException(SmsError.SMS_SEND_FAILURE);
            }
            
            return response;
        } catch (RestApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to send SMS: {}", e.getMessage());
            throw new RestApiException(SmsError.SMS_SEND_FAILURE);
        }
    }
}