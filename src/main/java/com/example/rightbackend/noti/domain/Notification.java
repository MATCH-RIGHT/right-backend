package com.example.rightbackend.noti.domain;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.noti.controller.dto.request.NotificationRequest;
import com.example.rightbackend.noti.controller.dto.response.NotificationResponse;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notificationId")
    private Long id;

    @Column
    private String content;

    @Column
    private boolean isRead = false;

    @Column
    private Long relatedId;

    @Column
    @Enumerated(EnumType.STRING)
    private NotificationCategory category;

    @Column(updatable = false)
    @CreatedDate
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    protected Notification(final NotificationCategory category,
                           final String content,
                           final Long relatedId,
                           final Member member) {
        this.category = category;
        this.content = content;
        this.member = member;
        this.relatedId = relatedId;
    }

    protected Notification() {
    }

    public static Notification of(final NotificationRequest request, Member member) {
        return new Notification(request.category(),
                request.content(),
                request.relatedId(),
                member);
    }

    public static Notification createLikeNotification(Member targetMember, Long matchingResultId, String sourceNickname) {
        String content = sourceNickname + "님이 회원님에게 좋아요를 보냈습니다.";
        return new Notification(NotificationCategory.LIKE, content, matchingResultId, targetMember);
    }

    public static Notification createMatchNotification(Member member, Long matchedId, String otherNickname) {
        String content = otherNickname + "님과 매칭되었습니다! 채팅방이 생성되었습니다.";
        return new Notification(NotificationCategory.MATCH, content, matchedId, member);
    }

    public static Notification createDailyMatchCompleteNotification(Member member, int matchCount) {
        String content = "오늘의 매칭이 완료되었습니다! " + matchCount + "개의 새로운 매칭을 확인해보세요.";
        return new Notification(NotificationCategory.DAILY_MATCH_COMPLETE, content, null, member);
    }

    public static Notification createChatMessageNotification(Member receiver, Long chatRoomId, String senderName, String messageContent) {
        String content = senderName + ": " + messageContent;
        return new Notification(NotificationCategory.CHAT_MESSAGE, content, chatRoomId, receiver);
    }

    public boolean isOwner(final Long memberId) {
        return member.getId().equals(memberId);
    }

    public void read() {
        isRead = true;
    }

    public NotificationResponse getNotificationResponse() {
        return new NotificationResponse(
                id,
                content,
                relatedId,
                NotificationType.PERSONAL,
                category,
                createdAt,
                isRead
        );
    }

    public Long getId() {
        return id;
    }
}