package com.example.rightbackend.matching.business.scheduler;

import com.example.rightbackend.matching.business.service.MatchingService;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.domain.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingScheduler {

    private final MatchingService matchingService;
    private final MemberProfileRepository memberProfileRepository;
    
    private static final String FREE_MATCHING_TYPE = "FREE";

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void executeDailyFreeMatching() {
        log.info("일일 무료 매칭 실행 시작");
        
        List<MemberProfile> memberProfiles = memberProfileRepository.findAll();
        
        int totalMatchings = 0;
        
        for (MemberProfile memberProfile : memberProfiles) {
            try {
                int matchCount = matchingService.executeMatching(memberProfile, FREE_MATCHING_TYPE).size();
                totalMatchings += matchCount;
                log.debug("회원 ID: {}, 매칭 수: {}", memberProfile.getId(), matchCount);
            } catch (Exception e) {
                log.error("회원 ID: {}의 매칭 중 오류 발생: {}", memberProfile.getId(), e.getMessage(), e);
            }
        }
        
        log.info("일일 무료 매칭 실행 완료. 총 매칭 수: {}", totalMatchings);
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void cleanupExpiredMatchings() {
        log.info("만료된 매칭 정리 시작");
        
        int cleanedCount = matchingService.cleanupExpiredMatchings();
        
        log.info("만료된 매칭 정리 완료. 삭제된 매칭 수: {}", cleanedCount);
    }
}
