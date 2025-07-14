package com.example.rightbackend.docs;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.NotificationSuccess;
import com.example.rightbackend.noti.controller.dto.response.NotificationResponse;
import com.example.rightbackend.noti.domain.NotificationCategory;
import com.example.rightbackend.noti.domain.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NotificationDocs extends BaseRestDocsTest {

    @Test
    @DisplayName("API - 알림 목록 조회")
    void getNotifications() throws Exception {
        List<NotificationResponse> notifications = createSampleNotifications();
        SuccessResponse<List<NotificationResponse>> response = SuccessResponse.of(NotificationSuccess.GET_NOTIFICATIONS_SUCCESS, notifications);

        doReturn(response).when(notificationController).getNotifications(any(LoginMember.class));

        this.mockMvc.perform(get("/api/notifications")
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("notification-get-notifications",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("알림 목록"),
                                fieldWithPath("result[].id").description("알림 ID"),
                                fieldWithPath("result[].content").description("알림 내용"),
                                fieldWithPath("result[].relatedId").description("관련 ID"),
                                fieldWithPath("result[].noticeType").description("알림 타입"),
                                fieldWithPath("result[].category").description("알림 카테고리"),
                                fieldWithPath("result[].createdAt").description("생성 시간"),
                                fieldWithPath("result[].isRead").description("읽음 여부")
                        )
                ));
    }

    @Test
    @DisplayName("API - 알림 읽음 처리")
    void readNotification() throws Exception {
        Long notificationId = 1L;
        SuccessResponse<Void> response = SuccessResponse.of(NotificationSuccess.READ_NOTIFICATION_SUCCESS);

        doReturn(response).when(notificationController).readNotification(any(LoginMember.class), any(Long.class));

        this.mockMvc.perform(patch("/api/notifications/{notificationId}", notificationId)
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("notification-read-notification",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("notificationId").description("알림 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("결과").optional()
                        )
                ));
    }

    @Test
    @DisplayName("API - 읽지 않은 알림 개수 조회")
    void countUnreadNotifications() throws Exception {
        Long count = 3L;
        SuccessResponse<Long> response = SuccessResponse.of(NotificationSuccess.COUNT_UNREAD_NOTIFICATIONS_SUCCESS, count);

        doReturn(response).when(notificationController).countUnreadNotifications(any(LoginMember.class));

        this.mockMvc.perform(get("/api/notifications/count")
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("notification-count-unread-notifications",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("읽지 않은 알림 개수")
                        )
                ));
    }

    @Test
    @DisplayName("API - FCM 토큰 등록")
    void registerFcmToken() throws Exception {
        // FCM 토큰을 JSON 객체로 감싸서 전송
        String fcmTokenJson = "{\"fcmToken\":\"fcmTokenExample\"}";
        SuccessResponse<Void> response = SuccessResponse.of(NotificationSuccess.REGISTER_FCM_TOKEN_SUCCESS);

        doReturn(response).when(notificationController).registerFcmToken(any(LoginMember.class), any(String.class));

        this.mockMvc.perform(post("/api/notifications/fcm-token")
                        .header("Authorization", GIVEN_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(fcmTokenJson))
                .andExpect(status().isOk())
                .andDo(document("notification-register-fcm-token",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("fcmToken").description("FCM 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("결과").optional()
                        )
                ));
    }

    @Test
    @DisplayName("API - FCM 상태 조회")
    void getFcmStatus() throws Exception {
        Boolean status = true;
        SuccessResponse<Boolean> response = SuccessResponse.of(NotificationSuccess.GET_FCM_STATUS_SUCCESS, status);

        doReturn(response).when(notificationController).getFcmStatus(any(LoginMember.class));

        this.mockMvc.perform(get("/api/notifications/fcm-status")
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("notification-get-fcm-status",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("FCM 알림 수신 상태 (true: 수신, false: 수신 거부)")
                        )
                ));
    }

    @Test
    @DisplayName("API - FCM 상태 변경")
    void changeFcmStatus() throws Exception {
        Boolean decision = true;
        SuccessResponse<Void> response = SuccessResponse.of(NotificationSuccess.CHANGE_FCM_STATUS_SUCCESS);

        doReturn(response).when(notificationController).changeFcmStatus(any(LoginMember.class), any(Boolean.class));

        String requestBody = String.valueOf(decision);
        
        this.mockMvc.perform(patch("/api/notifications/fcm-status")
                        .header("Authorization", GIVEN_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(document("notification-change-fcm-status",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        requestBody(),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("결과").optional()
                        )
                ));
    }

    private List<NotificationResponse> createSampleNotifications() {
        List<NotificationResponse> notifications = new ArrayList<>();
        
        notifications.add(new NotificationResponse(
                1L,
                "새로운 매칭이 성사되었습니다.",
                1L,
                NotificationType.PERSONAL,
                NotificationCategory.MATCH,
                Instant.now().minusSeconds(3600),
                false
        ));
        
        notifications.add(new NotificationResponse(
                2L,
                "프로필이 성공적으로 업데이트되었습니다.",
                2L,
                NotificationType.PERSONAL,
                NotificationCategory.LIKE,
                Instant.now().minusSeconds(86400),
                true
        ));
        
        notifications.add(new NotificationResponse(
                3L,
                "서비스 점검 안내입니다.",
                3L,
                NotificationType.PUBLIC,
                NotificationCategory.MATCH,
                Instant.now().minusSeconds(172800),
                false
        ));
        
        return notifications;
    }
}