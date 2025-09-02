package com.example.rightbackend.noti.service;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.FCMError;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.matching.business.domain.Matched;
import com.example.rightbackend.matching.business.domain.MatchingResult;
import com.example.rightbackend.noti.domain.FCMToken;
import com.example.rightbackend.noti.domain.repository.FCMTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FcmSender {
    private final static String RIGHT_TITLE = "Right";
    private final static String LIKE_MESSAGE_BODY = "님이 회원님에게 좋아요를 보냈습니다.";
    private final static String MATCHING_SUCCESS_MESSAGE_BODY = "님과 매칭되었습니다! 채팅방이 생성되었습니다.";
    private final static String DAILY_MATCH_COMPLETE_MESSAGE_BODY = "개의 새로운 매칭을 확인해보세요.";

    private final MemberRepository memberRepository;
    private final FCMTokenRepository fcmTokenRepository;

    public FcmSender(MemberRepository memberRepository,
                     FCMTokenRepository fcmTokenRepository) {
        this.memberRepository = memberRepository;
        this.fcmTokenRepository = fcmTokenRepository;
    }

    @Async
    public void sendLikeNotification(MatchingResult matchingResult) {
        Member targetMember = matchingResult.getTargetMemberProfile().getMember();
        String sourceNickname = matchingResult.getSourceMemberProfile().getDecryptedNickname();

        if(!isAcceptedFCM(targetMember)) return;

        Notification notification = Notification.builder()
                .setTitle(RIGHT_TITLE)
                .setBody(sourceNickname + LIKE_MESSAGE_BODY)
                .build();

        FCMToken fcmToken = findFcmToken(targetMember);

        Message message = Message.builder()
                .setToken(fcmToken.getToken())
                .setNotification(notification)
                .build();

        sendSingleFcm(message);
    }

    @Async
    public void sendMatchNotification(Matched matched) {
        Member member1 = matched.getMemberProfile1().getMember();
        String nickname2 = matched.getMemberProfile2().getDecryptedNickname();

        if(isAcceptedFCM(member1)) {
            Notification notification1 = Notification.builder()
                    .setTitle(RIGHT_TITLE)
                    .setBody(nickname2 + MATCHING_SUCCESS_MESSAGE_BODY)
                    .build();

            FCMToken fcmToken1 = findFcmToken(member1);

            Message message1 = Message.builder()
                    .setToken(fcmToken1.getToken())
                    .setNotification(notification1)
                    .build();

            sendSingleFcm(message1);
        }

        Member member2 = matched.getMemberProfile2().getMember();
        String nickname1 = matched.getMemberProfile1().getDecryptedNickname();

        if(isAcceptedFCM(member2)) {
            Notification notification2 = Notification.builder()
                    .setTitle(RIGHT_TITLE)
                    .setBody(nickname1 + MATCHING_SUCCESS_MESSAGE_BODY)
                    .build();

            FCMToken fcmToken2 = findFcmToken(member2);

            Message message2 = Message.builder()
                    .setToken(fcmToken2.getToken())
                    .setNotification(notification2)
                    .build();

            sendSingleFcm(message2);
        }
    }

    @Async
    public void sendChatMessage(com.example.rightbackend.chat.domain.Message message) {
        try {
            if (message == null || message.getReceiverId() == null) {
                log.warn("Invalid message object or receiver ID is null");
                return;
            }
            
            Member receiver = getMember(message.getReceiverId());
            if (!isAcceptedFCM(receiver)) {
                log.debug("FCM notifications disabled for receiver: {}", message.getReceiverId());
                return;
            }

            var fcmMessage = createFcmMessage(message);
            sendSingleFcm(fcmMessage);
            
            log.info("Chat message FCM sent successfully: receiverId={}, roomId={}", 
                    message.getReceiverId(), message.getRoomId());
                    
        } catch (Exception e) {
            log.error("Failed to send chat message FCM: receiverId={}, roomId={}", 
                    message.getReceiverId(), message.getRoomId(), e);
        }
    }

    private boolean isAcceptedFCM(Member receiver) {
        FCMToken fcmToken = findFcmToken(receiver);
        return fcmToken.isAccepted();
    }


    private com.google.firebase.messaging.Message createFcmMessage(com.example.rightbackend.chat.domain.Message request) {
        try {
            Member sender = getMember(request.getSenderId());
            Member receiver = getMember(request.getReceiverId());
            
            // 메시지 내용이 너무 길면 잘라서 표시
            String messageBody = request.getContent();
            if (messageBody.length() > 100) {
                messageBody = messageBody.substring(0, 100) + "...";
            }

            return com.google.firebase.messaging.Message.builder()
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(sender.getName())
                            .setBody(messageBody)
                            .build())
                    .setToken(findFcmToken(receiver).getToken())
                    .putData("type", "chat")
                    .putData("roomId", String.valueOf(request.getRoomId()))
                    .putData("senderId", String.valueOf(request.getSenderId()))
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to create FCM message: {}", e.getMessage(), e);
            throw new RestApiException(FCMError.FCM_MESSAGING_ERROR);
        }
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new RestApiException(MemberError.NULL_MEMBER));
    }

    private FCMToken findFcmToken(Member member) {
        return fcmTokenRepository.findByMember(member).orElseThrow(() -> new RestApiException(FCMError.FCM_MESSAGING_ERROR));
    }

    @Async
    public void sendDailyMatchCompleteNotification(Member member, int matchCount) {
        if (!isAcceptedFCM(member)) return;

        Notification notification = Notification.builder()
                .setTitle(RIGHT_TITLE)
                .setBody("오늘의 매칭이 완료되었습니다! " + matchCount + DAILY_MATCH_COMPLETE_MESSAGE_BODY)
                .build();

        FCMToken fcmToken = findFcmToken(member);

        Message message = Message.builder()
                .setToken(fcmToken.getToken())
                .setNotification(notification)
                .build();

        sendSingleFcm(message);
    }

    private void sendSingleFcm(Message message) {
        try {
            String messageId = FirebaseMessaging.getInstance().send(message);
            log.debug("FCM message sent successfully with ID: {}", messageId);
        } catch (Exception e) {
            log.error("Failed to send FCM message: {}", e.getMessage(), e);
            throw new RestApiException(FCMError.FCM_MESSAGING_ERROR);
        }
    }
}