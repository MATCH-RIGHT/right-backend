package com.example.rightbackend.notification.service;

import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.notification.domain.respository.FcmTokenRepository;
import org.springframework.stereotype.Component;

@Component
public class FCMSender {

    private final MemberRepository memberRepository;
    private final FcmTokenRepository fcmTokenRepository;

    public FCMSender(MemberRepository memberRepository, FcmTokenRepository fcmTokenRepository) {
        this.memberRepository = memberRepository;
        this.fcmTokenRepository = fcmTokenRepository;
    }
}