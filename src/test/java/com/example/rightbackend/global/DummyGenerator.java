package com.example.rightbackend.global;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.Token;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.auth.domain.repository.TokenRepository;
import com.example.rightbackend.auth.service.TokenProvider;
import com.example.rightbackend.member.controller.dto.EncodeMember;
import com.example.rightbackend.member.controller.dto.EncodeMemberProfile;
import com.example.rightbackend.member.domain.Interest;
import com.example.rightbackend.member.domain.Location;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.domain.repository.InterestRepository;
import com.example.rightbackend.member.domain.repository.LocationRepository;
import com.example.rightbackend.member.domain.repository.MemberProfileRepository;
import com.example.rightbackend.member.service.TextEncoder;
import com.example.rightbackend.noti.controller.dto.request.NotificationRequest;
import com.example.rightbackend.noti.domain.FCMToken;
import com.example.rightbackend.noti.domain.Notification;
import com.example.rightbackend.noti.domain.NotificationCategory;
import com.example.rightbackend.noti.domain.repository.FCMTokenRepository;
import com.example.rightbackend.noti.domain.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DummyGenerator {
    @Autowired MemberRepository memberRepository;
    @Autowired MemberProfileRepository memberProfileRepository;
    @Autowired InterestRepository interestRepository;
    @Autowired LocationRepository locationRepository;
    @Autowired TextEncoder textEncoder;
    @Autowired NotificationRepository notificationRepository;
    @Autowired FCMTokenRepository fcmTokenRepository;

    public static final String GIVEN_NAME = "tmpName";
    public static final String GIVEN_PROVIDER_ID = "testuser123";
    public static final String GIVEN_PASSWORD = "TestPass123!";
    public static final String GIVEN_PHONE_NUMBER = "010-1234-5678";
    public static final String GIVEN_NICKNAME = "tmpNicknmae";
    public static final String GIVEN_GENDER = "MALE";
    public static final String GIVEN_BIRTHDAY = "1999-01-01";
    public static final String GIVEN_LOCATION_NAME = "부산광역시 남구";
    public static final Integer GIVEN_HEIGHT = 180;
    public static final String GIVEN_BODY_TYPE = "slim";
    public static final String GIVEN_JOB = "student";
    public static final String GIVEN_MONEY = "0";
    public static final String GIVEN_INTRODUCTION = "hello world";
    public static final String GIVEN_NOTIFICATION_CONTENT = "알림 내용입니다";

    public static final List<Long> GIVEN_INTERESTS = List.of(1L, 2L, 3L);
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private TokenRepository tokenRepository;

    public Member generateSingleMember() {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        Member member = makeMember(uniqueSuffix);
        saveMemberProfile(member);
        return member;
    }

    private Member makeMember(String uniqueSuffix) {
        // uniqueSuffix에서 마지막 4자리만 사용하여 올바른 전화번호 형식 유지
        String phoneNumberSuffix = uniqueSuffix.length() >= 4 ? 
                uniqueSuffix.substring(uniqueSuffix.length() - 4) : 
                String.format("%04d", Integer.parseInt(uniqueSuffix) % 10000);
        String uniquePhoneNumber = "010-1234-" + phoneNumberSuffix;
        
        EncodeMember signUp = new EncodeMember(
                GIVEN_NAME + uniqueSuffix, 
                GIVEN_PROVIDER_ID + uniqueSuffix, 
                GIVEN_PASSWORD,
                TextEncoder.encrypt(uniquePhoneNumber));
        return memberRepository.save(Member.of(signUp));
    }

    private void saveMemberProfile(final Member member) {
        EncodeMemberProfile encodeMemberProfile = new EncodeMemberProfile(
                TextEncoder.encrypt(GIVEN_NICKNAME),
                TextEncoder.encrypt(GIVEN_GENDER),
                TextEncoder.encrypt(GIVEN_BIRTHDAY),
                GIVEN_HEIGHT,
                TextEncoder.encrypt(GIVEN_BODY_TYPE),
                TextEncoder.encrypt(GIVEN_JOB),
                GIVEN_INTERESTS,
                TextEncoder.encrypt(GIVEN_INTRODUCTION),
                TextEncoder.encrypt(GIVEN_MONEY));

        MemberProfile memberProfile = MemberProfile.of(encodeMemberProfile, member);

        addInterestsToMemberProfile(memberProfile, GIVEN_INTERESTS);

        Location location = locationRepository.findByName(GIVEN_LOCATION_NAME)
                .orElseGet(() -> locationRepository.save(Location.of(GIVEN_LOCATION_NAME)));
        memberProfile.addLocation(location);

        memberProfileRepository.save(memberProfile);
        member.setMemberProfile(memberProfile);
        memberRepository.save(member);
    }

    private void addInterestsToMemberProfile(MemberProfile memberProfile, List<Long> interestIds) {
        if(interestIds == null || interestIds.isEmpty()) {
            return;
        }
        // 테스트용 Interest 생성
        for(Long id: interestIds) {
            Interest interest = interestRepository.findById(id)
                .orElseGet(() -> {
                    Interest newInterest = Interest.of("Interest" + id);
                    newInterest.setId(id);
                    return interestRepository.save(newInterest);
                });
            memberProfile.addInterest(interest);
        }
    }

    public String generateAccessToken(Member member) {
        String accessToken = tokenProvider.generateAccessToken(member.getLoginMember());
        Token refreshToken = new Token(member);
        refreshToken.setRefreshToken(accessToken);
        tokenRepository.save(refreshToken);
        return accessToken;
    }

    public Notification generateSingleNotification(Member member) {
        NotificationRequest request = new NotificationRequest(
                NotificationCategory.LIKE,
                1L,
                GIVEN_NOTIFICATION_CONTENT,
                List.of(member)
        );
        
        Notification notification = Notification.of(request, member);
        return notificationRepository.save(notification);
    }
    
    public List<Notification> generateNotificationWithCount(Member member, int count) {
        List<Notification> notifications = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            NotificationRequest request = new NotificationRequest(
                    NotificationCategory.LIKE,
                    (long) i,
                    GIVEN_NOTIFICATION_CONTENT + " " + i,
                    List.of(member)
            );
            
            Notification notification = Notification.of(request, member);
            notifications.add(notificationRepository.save(notification));
        }
        
        return notifications;
    }
    
    public FCMToken generateFcmToken(Member member) {
        FCMToken fcmToken = FCMToken.of(member, "fcmTokenExample");
        
        return fcmTokenRepository.save(fcmToken);
    }
}