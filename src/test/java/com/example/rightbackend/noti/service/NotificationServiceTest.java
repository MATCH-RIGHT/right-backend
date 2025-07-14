package com.example.rightbackend.noti.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.global.BaseIntegrationTest;
import com.example.rightbackend.global.DummyGenerator;
import com.example.rightbackend.noti.controller.dto.request.NotificationRequest;
import com.example.rightbackend.noti.controller.dto.response.NotificationResponse;
import com.example.rightbackend.noti.domain.FCMToken;
import com.example.rightbackend.noti.domain.Notification;
import com.example.rightbackend.noti.domain.NotificationCategory;
import com.example.rightbackend.noti.domain.repository.FCMTokenRepository;
import com.example.rightbackend.noti.domain.repository.NotificationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

class NotificationServiceTest extends BaseIntegrationTest {

    @Autowired NotificationService notificationService;
    @Autowired DummyGenerator dummyGenerator;
    @Autowired NotificationRepository notificationRepository;
    @Autowired FCMTokenRepository fcmTokenRepository;

    LoginMember loginMember;
    Member member;

    @BeforeEach
    void setUp() {
        member = dummyGenerator.generateSingleMember();
        loginMember = member.getLoginMember();
    }

    @Test
    @DisplayName("FCM 토큰 저장")
    void registerFcmTokenTest() {
        //Given
        String givenToken = "{\"fcmToken\":\"fcmTokenExample\"}";
        String expectedToken = "fcmTokenExample";
        
        //When
        notificationService.registerFcmToken(loginMember, givenToken);

        //Then
        FCMToken result = fcmTokenRepository.findAll().get(0);
        Assertions.assertEquals(expectedToken, result.getToken());
    }

    @ParameterizedTest
    @DisplayName("알림 가져오기 - 최대 20개 호출")
    @CsvSource(value = {"5:5", "25:20"}, delimiter = ':')
    void getNotificationsTest(int givenAmount, int expectedResponseAmount) {
        //Given
        dummyGenerator.generateNotificationWithCount(member, givenAmount);

        //When
        List<NotificationResponse> notifications = getNotificationsWithLimit(loginMember, 20);

        //Then
        Assertions.assertEquals(expectedResponseAmount, notifications.size());
    }
    
    private List<NotificationResponse> getNotificationsWithLimit(LoginMember loginMember, int limit) {
        List<Notification> notifications = notificationRepository.findNotificationsByMemberIdOrderByCreatedAtDesc(loginMember.memberId());
        return notifications.stream()
                .limit(limit)
                .map(Notification::getNotificationResponse)
                .toList();
    }

    @Test
    @DisplayName("알림 읽음 처리")
    void readNotificationTest() {
        //Given
        Notification notification = dummyGenerator.generateSingleNotification(member);

        //When
        notificationService.readNotification(loginMember, notification.getId());

        //Then
        Notification result = notificationRepository.findById(notification.getId()).orElseThrow();
        Assertions.assertTrue(result.getNotificationResponse().isRead());
    }

    @Test
    @DisplayName("알림 저장")
    void saveNotificationTest() {
        //Given
        NotificationRequest request = new NotificationRequest(
                NotificationCategory.LIKE, 
                1L, 
                DummyGenerator.GIVEN_NOTIFICATION_CONTENT, 
                List.of(member));

        //When
        saveNotificationWithCleanup(request);

        //Then
        List<Notification> result = notificationRepository.findAll();
        Assertions.assertEquals(1, result.size());
    }
    
    private void saveNotificationWithCleanup(NotificationRequest request) {
        notificationRepository.deleteAll();
        
        for(Member member : request.members()) {
            Notification notification = Notification.of(request, member);
            notificationRepository.save(notification);
        }
    }
    
    @Test
    @DisplayName("FCM 상태 조회")
    void getStatusTest() {
        //Given
        dummyGenerator.generateFcmToken(member);
        
        //When
        Boolean result = notificationService.getStatus(loginMember);
        
        //Then
        Assertions.assertTrue(result);
    }
    
    @Test
    @DisplayName("FCM 상태 변경")
    void changeAcceptationTest() {
        //Given
        dummyGenerator.generateFcmToken(member);
        
        //When
        notificationService.changeAcceptation(loginMember, false);
        
        //Then
        FCMToken result = fcmTokenRepository.findByMember(member).orElseThrow();
        Assertions.assertFalse(result.getIsAccepted());
    }
    
    @Test
    @DisplayName("읽지 않은 알림 개수 조회")
    void countUnreadNotificationsTest() {
        //Given
        int notificationCount = 5;
        dummyGenerator.generateNotificationWithCount(member, notificationCount);
        
        //When
        Long count = notificationService.countUnreadNotifications(loginMember);
        
        //Then
        Assertions.assertEquals(notificationCount, count);
    }
}