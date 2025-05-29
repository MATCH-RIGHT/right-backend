package com.example.rightbackend.matching.business.service;

import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.MatchingError;
import com.example.rightbackend.matching.business.domain.MatchingResult;
import com.example.rightbackend.matching.business.domain.repository.MatchingResultRepository;
import com.example.rightbackend.matching.business.type.MatchingType;
import com.example.rightbackend.member.domain.MemberProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MatchingResultRepository matchingResultRepository;
    private final Map<String, MatchingType> matchingTypes;

    @Transactional
    public List<MatchingResult> executeMatching(MemberProfile memberProfile, String matchingTypeName) {
        MatchingType matchingType = matchingTypes.get(matchingTypeName.toUpperCase());
        if (matchingType == null) {
            throw new RestApiException(MatchingError.UNSUPPORTED_MATCHING_TYPE);
        }
        
        List<MemberProfile> matchedProfiles = matchingType.executeMatching(memberProfile);
        
        return matchedProfiles.stream()
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
    }

    @Transactional
    public MatchingResult like(Long matchingResultId, MemberProfile memberProfile) {
        MatchingResult matchingResult = matchingResultRepository.findById(matchingResultId)
                .orElseThrow(() -> new RestApiException(MatchingError.MATCHING_RESULT_NOT_FOUND));
        
        if (matchingResult.isExpired()) {
            throw new RestApiException(MatchingError.EXPIRED_MATCHING);
        }
        
        if (matchingResult.getSourceMemberProfile().getId().equals(memberProfile.getId())) {
            matchingResult.sourceLike();
        } else if (matchingResult.getTargetMemberProfile().getId().equals(memberProfile.getId())) {
            matchingResult.targetLike();
        } else {
            throw new RestApiException(MatchingError.UNAUTHORIZED_USER);
        }
        
        return matchingResultRepository.save(matchingResult);
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

    @Transactional
    public int cleanupExpiredMatchings() {
        List<MatchingResult> expiredMatchings = matchingResultRepository
                .findByExpiresAtLessThanAndMatchedFalse(LocalDateTime.now());
        
        matchingResultRepository.deleteAll(expiredMatchings);
        
        return expiredMatchings.size();
    }
}
