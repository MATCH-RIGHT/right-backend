package com.example.rightbackend.sms.service;

import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.SmsError;
import com.example.rightbackend.sms.controller.dto.request.SmsConfirmRequest;
import com.example.rightbackend.sms.controller.dto.request.SmsSendRequest;
import com.example.rightbackend.sms.controller.dto.response.SmsResponse;
import com.example.rightbackend.sms.domain.repository.SmsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SmsService {

    private final SmsUtils smsUtils;
    private final SmsRepository smsRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public String sendSms(SmsSendRequest smsRequest) {
        String to = smsRequest.phoneNumber();
        String certificationNumber = generateSecureCertificationNumber();
        
        try {
            SingleMessageSentResponse response = smsUtils.sendSMS(to, certificationNumber);
            log.info("SMS sent successfully to {} with statusCode: {}", to, response.getStatusCode());
            
            smsRepository.createSmsCertification(to, certificationNumber);
            return SmsResponse.SEND_VERIFICATION_CODE_SUCCESS.getMessage();
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", to, e.getMessage());
            throw new RestApiException(SmsError.SMS_SEND_FAILURE);
        }
    }
    
    private String generateSecureCertificationNumber() {
        return String.format("%06d", secureRandom.nextInt(1000000));
    }

    public String verifySms(SmsConfirmRequest smsRequest) {
        try {
            if(!isVerify(smsRequest)) {
                throw new RestApiException(SmsError.INVALID_CERTIFICATION_CODE);
            }
            smsRepository.removeSmsCertification(smsRequest.phoneNumber());
            log.info("SMS verification successful for phone number: {}", smsRequest.phoneNumber());
            return SmsResponse.EQUAL_VERIFICATION_CODE_SUCCESS.getMessage();
        } catch (RestApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to verify SMS for {}: {}", smsRequest.phoneNumber(), e.getMessage());
            throw new RestApiException(SmsError.REDIS_CONNECTION_ERROR);
        }
    }

    private boolean isVerify(SmsConfirmRequest smsRequest) {
        try {
            if(!smsRepository.hasKey(smsRequest.phoneNumber())) {
                log.warn("No certification code found for phone number: {}", smsRequest.phoneNumber());
                return false;
            }
            
            String storedCertificationNumber = smsRepository.getSmsCertification(smsRequest.phoneNumber());
            if (storedCertificationNumber == null) {
                log.warn("Certification code expired for phone number: {}", smsRequest.phoneNumber());
                throw new RestApiException(SmsError.CERTIFICATION_CODE_EXPIRED);
            }
            
            return storedCertificationNumber.equals(smsRequest.certificationNumber());
        } catch (RestApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error checking certification code for {}: {}", smsRequest.phoneNumber(), e.getMessage());
            throw new RestApiException(SmsError.REDIS_CONNECTION_ERROR);
        }
    }
}