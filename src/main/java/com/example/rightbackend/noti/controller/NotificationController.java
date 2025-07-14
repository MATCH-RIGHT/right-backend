package com.example.rightbackend.noti.controller;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.global.config.resolver.Login;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.NotificationSuccess;
import com.example.rightbackend.noti.controller.dto.response.NotificationResponse;
import com.example.rightbackend.noti.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<NotificationResponse>>> getNotifications(@Login LoginMember loginMember) {
        List<NotificationResponse> notifications = notificationService.getNotifications(loginMember);
        return SuccessResponse.of(NotificationSuccess.GET_NOTIFICATIONS_SUCCESS, notifications);
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<SuccessResponse<Void>> readNotification(@Login LoginMember loginMember,
                                                             @PathVariable Long notificationId) {
        notificationService.readNotification(loginMember, notificationId);
        return SuccessResponse.of(NotificationSuccess.READ_NOTIFICATION_SUCCESS);
    }

    @GetMapping("/count")
    public ResponseEntity<SuccessResponse<Long>> countUnreadNotifications(@Login LoginMember loginMember) {
        Long count = notificationService.countUnreadNotifications(loginMember);
        return SuccessResponse.of(NotificationSuccess.COUNT_UNREAD_NOTIFICATIONS_SUCCESS, count);
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<SuccessResponse<Void>> registerFcmToken(@Login LoginMember loginMember,
                                                             @RequestBody String fcmToken) {
        notificationService.registerFcmToken(loginMember, fcmToken);
        return SuccessResponse.of(NotificationSuccess.REGISTER_FCM_TOKEN_SUCCESS);
    }

    @GetMapping("/fcm-status")
    public ResponseEntity<SuccessResponse<Boolean>> getFcmStatus(@Login LoginMember loginMember) {
        Boolean status = notificationService.getStatus(loginMember);
        return SuccessResponse.of(NotificationSuccess.GET_FCM_STATUS_SUCCESS, status);
    }

    @PatchMapping("/fcm-status")
    public ResponseEntity<SuccessResponse<Void>> changeFcmStatus(@Login LoginMember loginMember,
                                                            @RequestBody Boolean decision) {
        notificationService.changeAcceptation(loginMember, decision);
        return SuccessResponse.of(NotificationSuccess.CHANGE_FCM_STATUS_SUCCESS);
    }
}