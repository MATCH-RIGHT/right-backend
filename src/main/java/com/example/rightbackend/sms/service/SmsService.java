package com.example.rightbackend.sms.service;

import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.SmsError;
import com.example.rightbackend.sms.controller.dto.request.SmsConfirmRequest;
import com.example.rightbackend.sms.controller.dto.request.SmsSendRequest;
import com.example.rightbackend.sms.controller.dto.response.SmsResponse;
import com.example.rightbackend.sms.domain.repository.SmsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SmsService {

    private final SmsUtils smsUtils;
    private final SmsRepository smsRepository;

    public String sendSms(SmsSendRequest smsRequest) {
        String to = smsRequest.phoneNumber();
        int randomNumber = (int)(Math.random() * 9000) + 1000;
        String certificationNumber = String.valueOf(randomNumber);
        smsUtils.sendSMS(to, certificationNumber);
        smsRepository.createSmsCertification(to, certificationNumber);
        return SmsResponse.SEND_VERIFICATION_CODE_SUCCESS.getMessage();
    }

    public String verifySms(SmsConfirmRequest smsRequest) {
        if(!isVerify(smsRequest)) {
            throw new RestApiException(SmsError.INVALID_CERTIFICATION_CODE);
        }
        smsRepository.removeSmsCertification(smsRequest.phoneNumber());
        return SmsResponse.EQUAL_VERIFICATION_CODE_SUCCESS.getMessage();
    }

    private boolean isVerify(SmsConfirmRequest smsRequest) {
        if(!smsRepository.hasKey(smsRequest.phoneNumber())) {
            return false;
        }
        
        String storedCertificationNumber = smsRepository.getSmsCertification(smsRequest.phoneNumber());
        return storedCertificationNumber != null && storedCertificationNumber.equals(smsRequest.certificationNumber());
    }
}