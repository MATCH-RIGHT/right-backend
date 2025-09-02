package com.example.rightbackend.noti.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.FCMError;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.global.response.error.NotificationError;
import com.example.rightbackend.matching.business.domain.Matched;
import com.example.rightbackend.matching.business.domain.MatchingResult;
import com.example.rightbackend.noti.controller.dto.request.NotificationRequest;
import com.example.rightbackend.noti.controller.dto.response.NotificationResponse;
import com.example.rightbackend.noti.domain.FCMToken;
import com.example.rightbackend.noti.domain.Notification;
import com.example.rightbackend.noti.domain.repository.FCMTokenRepository;
import com.example.rightbackend.noti.domain.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    public NotificationService(NotificationRepository notificationRepository, 
                              FCMTokenRepository fcmTokenRepository, 
                              MemberRepository memberRepository) {
        this.notificationRepository = notificationRepository;
        this.fcmTokenRepository = fcmTokenRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void registerFcmToken(final LoginMember loginMember, final String fcmToken) {
        Member member = getMember(loginMember.memberId());
        String parsedFcmToken = parseFcmToken(fcmToken);
        Optional<FCMToken> optionalFCMToken = fcmTokenRepository.findByMember(member);

        if (optionalFCMToken.isPresent()) {
            FCMToken fcmTokenEntity = optionalFCMToken.get();
            fcmTokenEntity.setToken(parsedFcmToken);
            return;
        }

        FCMToken fcmTokenEntity = FCMToken.of(member, parsedFcmToken);
        fcmTokenRepository.save(fcmTokenEntity);
    }

    private String parseFcmToken(final String fcmToken) {
        return fcmToken.substring(13, fcmToken.length() - 2);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(final LoginMember loginMember) {
        List<Notification> notifications = notificationRepository.findNotificationsByMemberIdOrderByCreatedAtDesc(loginMember.memberId());
        return notifications.stream()
                .map(Notification::getNotificationResponse)
                .toList();
    }

    @Transactional
    public void readNotification(final LoginMember loginMember, final Long notificationId) {
        Notification notification = getNotification(notificationId);

        if(!notification.isOwner(loginMember.memberId())) {
            throw new RestApiException(NotificationError.IS_NOT_OWNER);
        }

        notification.read();
    }

    private Notification getNotification(final Long notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow(() -> new RestApiException(NotificationError.NULL_NOTIFICATION));
    }

    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new RestApiException(MemberError.NULL_MEMBER));
    }

    @Transactional
    public void saveNotification(NotificationRequest request) {
        for(Member member : request.members()) {
            Notification notification = Notification.of(request, member);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void saveLikeNotification(MatchingResult matchingResult) {
        Member targetMember = matchingResult.getTargetMemberProfile().getMember();
        String sourceNickname = matchingResult.getSourceMemberProfile().getDecryptedNickname();
        
        Notification notification = Notification.createLikeNotification(targetMember, matchingResult.getId(), sourceNickname);
        notificationRepository.save(notification);
    }

    @Transactional
    public void saveMatchNotification(Matched matched) {
        Member member1 = matched.getMemberProfile1().getMember();
        String nickname2 = matched.getMemberProfile2().getDecryptedNickname();
        Notification notification1 = Notification.createMatchNotification(member1, matched.getId(), nickname2);
        notificationRepository.save(notification1);
        
        Member member2 = matched.getMemberProfile2().getMember();
        String nickname1 = matched.getMemberProfile1().getDecryptedNickname();
        Notification notification2 = Notification.createMatchNotification(member2, matched.getId(), nickname1);
        notificationRepository.save(notification2);
    }

    @Transactional
    public void saveDailyMatchCompleteNotification(Member member, int matchCount) {
        if (matchCount > 0) {
            Notification notification = Notification.createDailyMatchCompleteNotification(member, matchCount);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void saveChatMessageNotification(Member receiver, Long chatRoomId, String senderName, String messageContent) {
        try {
            if (receiver == null || chatRoomId == null || senderName == null || messageContent == null) {
                log.warn("Invalid parameters for chat message notification");
                return;
            }
            
            // 메시지 내용이 너무 길면 잘라서 저장
            String truncatedContent = messageContent.length() > 50 ? 
                    messageContent.substring(0, 50) + "..." : messageContent;
            
            Notification notification = Notification.createChatMessageNotification(
                    receiver, chatRoomId, senderName, truncatedContent);
            notificationRepository.save(notification);
            
            log.info("Chat message notification saved successfully: receiverId={}, chatRoomId={}, senderName={}", 
                    receiver.getId(), chatRoomId, senderName);
                    
        } catch (Exception e) {
            // 채팅 메시지 알림 저장 실패는 전체 프로세스를 중단하지 않음
            log.error("Failed to save chat message notification: receiverId={}, chatRoomId={}, error={}", 
                    receiver != null ? receiver.getId() : null, chatRoomId, e.getMessage());
        }
    }

    private FCMToken getFcmTokenByMember(final Member member) {
        return fcmTokenRepository.findByMember(member).orElseThrow(()
                -> new RestApiException(FCMError.FCM_MESSAGING_ERROR));
    }

    @Transactional(readOnly = true)
    public Boolean getStatus(final LoginMember loginMember) {
        Member member = getMember(loginMember.memberId());
        FCMToken token = getFcmTokenByMember(member);
        return token.getIsAccepted();
    }

    @Transactional
    public void changeAcceptation(final LoginMember loginMember, final Boolean decision) {
        Member member = getMember(loginMember.memberId());
        FCMToken token = getFcmTokenByMember(member);
        token.changeAccept(decision);
    }
    
    @Transactional(readOnly = true)
    public Long countUnreadNotifications(final LoginMember loginMember) {
        return notificationRepository.countByMemberIdAndIsReadFalse(loginMember.memberId());
    }
}