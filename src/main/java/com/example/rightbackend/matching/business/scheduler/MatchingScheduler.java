package com.example.rightbackend.matching.business.scheduler;

import com.example.rightbackend.matching.business.service.MatchingService;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.domain.repository.MemberProfileRepository;
import com.example.rightbackend.noti.service.FcmSender;
import com.example.rightbackend.noti.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingScheduler {

    private final MatchingService matchingService;
    private final MemberProfileRepository memberProfileRepository;
    private final NotificationService notificationService;
    private final FcmSender fcmSender;
    
    private static final String FREE_MATCHING_TYPE = "FREE";

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void executeDailyFreeMatching() {
        log.info("일일 무료 매칭 실행 시작");
        
        try {
            List<MemberProfile> memberProfiles = memberProfileRepository.findAll();
            
            if (memberProfiles.isEmpty()) {
                log.warn("매칭 실행할 회원 프로필이 없습니다");
                return;
            }
            
            int totalMatchings = 0;
            int successfulMatchings = 0;
            int failedMatchings = 0;
            Map<Long, Integer> memberMatchCounts = new HashMap<>();
            
            for (MemberProfile memberProfile : memberProfiles) {
                try {
                    int matchCount = matchingService.executeMatching(memberProfile, FREE_MATCHING_TYPE).size();
                    totalMatchings += matchCount;
                    successfulMatchings++;
                    memberMatchCounts.put(memberProfile.getId(), matchCount);
                    log.debug("회원 ID: {}, 매칭 수: {}", memberProfile.getId(), matchCount);
                } catch (Exception e) {
                    failedMatchings++;
                    log.error("회원 ID: {}의 매칭 중 오류 발생: {}", memberProfile.getId(), e.getMessage(), e);
                }
            }
            
            // 매칭 완료 알림 발송
            sendDailyMatchCompleteNotifications(memberMatchCounts);
            
            log.info("일일 무료 매칭 실행 완료. 총 매칭 수: {}, 성공: {}, 실패: {}", 
                    totalMatchings, successfulMatchings, failedMatchings);
                    
        } catch (Exception e) {
            log.error("일일 무료 매칭 실행 중 전체 오류 발생", e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredMatchingsAndNotify() {
        log.info("매칭 완료 처리 및 알림 전송 시작");
        
        try {
            int cleanedCount = matchingService.cleanupExpiredMatchings();
            log.info("매칭 완료 처리 완료. 처리된 매칭 수: {}", cleanedCount);
        } catch (Exception e) {
            log.error("매칭 완료 처리 중 오류 발생", e);
        }
    }
    
    private void sendDailyMatchCompleteNotifications(Map<Long, Integer> memberMatchCounts) {
        for (Map.Entry<Long, Integer> entry : memberMatchCounts.entrySet()) {
            Long memberProfileId = entry.getKey();
            Integer matchCount = entry.getValue();
            
            try {
                // MemberProfile을 통해 Member 조회
                MemberProfile memberProfile = memberProfileRepository.findById(memberProfileId).orElse(null);
                if (memberProfile != null && memberProfile.getMember() != null) {
                    notificationService.saveDailyMatchCompleteNotification(memberProfile.getMember(), matchCount);
                    fcmSender.sendDailyMatchCompleteNotification(memberProfile.getMember(), matchCount);
                }
            } catch (Exception e) {
                log.error("회원 ID: {}의 매칭 완료 알림 전송 중 오류 발생: {}", memberProfileId, e.getMessage());
            }
        }
    }
}
