package com.example.rightbackend.notification.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.notification.domain.FcmToken;
import com.example.rightbackend.notification.domain.respository.FcmTokenRepository;
import com.example.rightbackend.notification.domain.respository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    public NotificationService(NotificationRepository notificationRepository, FcmTokenRepository fcmTokenRepository, MemberRepository memberRepository) {
        this.notificationRepository = notificationRepository;
        this.fcmTokenRepository = fcmTokenRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void saveFcmToken(final LoginMember loginMember, final String fcmToken) {
        Member member = getMember(loginMember.memberId());
        String parsedFcmToken = parseFcmToken(fcmToken);
        Optional<FcmToken> optionalFcmToken = fcmTokenRepository.findByMember(member);

        if (optionalFcmToken.isPresent()) {
            FcmToken fcmTokenEntity = optionalFcmToken.get();
            fcmTokenEntity.setToken(parsedFcmToken);;
            return;
        }
        FcmToken fcmTokenEntity = FcmToken.of(member, parsedFcmToken);
        fcmTokenRepository.save(fcmTokenEntity);
    }

    private String parseFcmToken(final String fcmToken) {
        return fcmToken.substring(13, fcmToken.length()-2);
    }

    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new RestApiException(MemberError.NULL_MEMBER));
    }
}