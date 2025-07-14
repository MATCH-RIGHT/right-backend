package com.example.rightbackend.matching.business.service;

import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.MatchingError;
import com.example.rightbackend.matching.business.domain.Matched;
import com.example.rightbackend.matching.business.domain.MatchingResult;
import com.example.rightbackend.matching.business.domain.repository.MatchedRepository;
import com.example.rightbackend.matching.business.domain.repository.MatchingResultRepository;
import com.example.rightbackend.matching.business.type.MatchingType;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.noti.service.FcmSender;
import com.example.rightbackend.noti.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final MatchingResultRepository matchingResultRepository;
    private final MatchedRepository matchedRepository;
    private final Map<String, MatchingType> matchingTypes;
    private final NotificationService notificationService;
    private final FcmSender fcmSender;

    @Transactional
    public List<MatchingResult> executeMatching(MemberProfile memberProfile, String matchingTypeName) {
        try {
            MatchingType matchingType = matchingTypes.get(matchingTypeName.toUpperCase());
            if (matchingType == null) {
                log.error("Unsupported matching type: {}", matchingTypeName);
                throw new RestApiException(MatchingError.UNSUPPORTED_MATCHING_TYPE);
            }
            
            List<MemberProfile> matchedProfiles = matchingType.executeMatching(memberProfile);
            
            if (matchedProfiles.isEmpty()) {
                log.warn("No matching profiles found for member: {}", memberProfile.getId());
                throw new RestApiException(MatchingError.INSUFFICIENT_MEMBERS);
            }
            
            List<MatchingResult> results = matchedProfiles.stream()
                    .map(targetProfile -> {
                        Optional<MatchingResult> existingMatch = matchingResultRepository.findActiveMatchingBetweenProfiles(
                                memberProfile, targetProfile, LocalDateTime.now());
                        
                        if (existingMatch.isPresent()) {
                            return existingMatch.get();
                        }
                        
                        int compatibilityScore = matchingType.calculateCompatibility(memberProfile, targetProfile);
                        MatchingResult result = MatchingResult.of(
                                memberProfile, targetProfile, compatibilityScore, matchingType.getTypeName());
                        
                        return matchingResultRepository.save(result);
                    })
                    .collect(Collectors.toList());
            
            log.info("Matching executed successfully for member: {}, results: {}", memberProfile.getId(), results.size());
            return results;
            
        } catch (RestApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Matching execution failed for member: {}", memberProfile.getId(), e);
            throw new RestApiException(MatchingError.MATCHING_EXECUTION_FAILED);
        }
    }

    @Transactional
    public MatchingResult like(Long matchingResultId, MemberProfile memberProfile) {
        try {
            MatchingResult matchingResult = matchingResultRepository.findById(matchingResultId)
                    .orElseThrow(() -> new RestApiException(MatchingError.MATCHING_RESULT_NOT_FOUND));
            
            if (matchingResult.isExpired()) {
                throw new RestApiException(MatchingError.EXPIRED_MATCHING);
            }
            
            boolean isSourceMember = matchingResult.getSourceMemberProfile().getId().equals(memberProfile.getId());
            boolean isTargetMember = matchingResult.getTargetMemberProfile().getId().equals(memberProfile.getId());
            
            if (!isSourceMember && !isTargetMember) {
                throw new RestApiException(MatchingError.UNAUTHORIZED_USER);
            }
            
            if (isSourceMember) {
                if (matchingResult.isSourceLiked()) {
                    throw new RestApiException(MatchingError.DUPLICATE_LIKE_REQUEST);
                }
                matchingResult.sourceLike();
                log.info("Source member liked: matchingResultId={}, memberId={}", matchingResultId, memberProfile.getId());
            } else {
                if (matchingResult.isTargetLiked()) {
                    throw new RestApiException(MatchingError.DUPLICATE_LIKE_REQUEST);
                }
                matchingResult.targetLike();
                log.info("Target member liked: matchingResultId={}, memberId={}", matchingResultId, memberProfile.getId());
            }
            
            MatchingResult savedResult = matchingResultRepository.save(matchingResult);
            
            // 좋아요 알림 전송
            try {
                notificationService.saveLikeNotification(savedResult);
                fcmSender.sendLikeNotification(savedResult);
            } catch (Exception e) {
                log.error("Failed to send like notification: {}", e.getMessage());
            }
            
            // 매칭 완료 시 알림 전송
            if (savedResult.isSourceLiked() && savedResult.isTargetLiked()) {
                try {
                    Matched matched = Matched.fromMatchingResult(savedResult);
                    notificationService.saveMatchNotification(matched);
                    fcmSender.sendMatchNotification(matched);
                    log.info("Match completed and notification sent: matchingResultId={}", matchingResultId);
                } catch (Exception e) {
                    log.error("Failed to send match notification: {}", e.getMessage());
                }
            }
            
            return savedResult;
            
        } catch (RestApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Like operation failed: matchingResultId={}, memberId={}", matchingResultId, memberProfile.getId(), e);
            throw new RestApiException(MatchingError.MATCHING_EXECUTION_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public List<MatchingResult> getActiveMatchings(MemberProfile memberProfile) {
        return matchingResultRepository.findActiveMatchingsByMemberProfile(
                memberProfile, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<MatchingResult> getMatchedResults(MemberProfile memberProfile) {
        return matchingResultRepository.findMatchedResults(memberProfile);
    }
    
    @Transactional(readOnly = true)
    public List<Matched> getPermanentMatches(MemberProfile memberProfile) {
        return matchedRepository.findByMemberProfile(memberProfile);
    }
    
    @Transactional(readOnly = true)
    public List<Matched> getPermanentMatchesByMemberId(Long memberId) {
        return matchedRepository.findByMemberId(memberId);
    }

    @Transactional
    public int cleanupExpiredMatchings() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<MatchingResult> expiredMatchings = matchingResultRepository
                    .findByExpiresAtLessThanAndMatchedFalse(now);
            
            log.info("만료된 매칭 수: {}", expiredMatchings.size());
            
            List<Matched> newMatches = new ArrayList<>();
            List<MatchingResult> toDelete = new ArrayList<>();
            
            for (MatchingResult result : expiredMatchings) {
                if (result.isSourceLiked() && result.isTargetLiked()) {
                    Matched matched = Matched.fromMatchingResult(result);
                    newMatches.add(matched);
                    
                    // 매칭 완료 알림 전송
                    try {
                        notificationService.saveMatchNotification(matched);
                        fcmSender.sendMatchNotification(matched);
                    } catch (Exception e) {
                        log.error("Failed to send match notification during cleanup: {}", e.getMessage());
                    }
                    
                    log.debug("매치 성공: 소스 ID {}, 타겟 ID {}", 
                            result.getSourceMemberProfile().getId(), 
                            result.getTargetMemberProfile().getId());
                }
                toDelete.add(result);
            }
            
            if (!newMatches.isEmpty()) {
                matchedRepository.saveAll(newMatches);
                log.info("새로운 매치 수: {}", newMatches.size());
            }
            
            if (!toDelete.isEmpty()) {
                matchingResultRepository.deleteAll(toDelete);
                log.info("삭제된 매칭 수: {}", toDelete.size());
            }
            
            return expiredMatchings.size();
            
        } catch (Exception e) {
            log.error("Failed to cleanup expired matchings: {}", e.getMessage(), e);
            throw new RestApiException(MatchingError.MATCHING_CLEANUP_FAILED);
        }
    }
}
