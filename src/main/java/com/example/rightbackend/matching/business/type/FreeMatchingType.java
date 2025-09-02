package com.example.rightbackend.matching.business.type;

import com.example.rightbackend.matching.filter.domain.MatchingFilter;
import com.example.rightbackend.matching.filter.domain.repository.MatchingFilterRepository;
import com.example.rightbackend.member.domain.MemberProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FreeMatchingType implements MatchingType {

    private final MatchingFilterRepository matchingFilterRepository;
    
    private static final String TYPE_NAME = "FREE";
    private static final int MAX_MATCHES = 3;

    @Override
    public List<MemberProfile> executeMatching(MemberProfile memberProfile) {
        MatchingFilter filter = matchingFilterRepository.findByMemberProfile(memberProfile)
                .orElse(null);
        
        if (filter == null) {
            return Collections.emptyList();
        }
        
        String oppositeGender = "MALE".equals(memberProfile.getDecryptedGender()) ? "FEMALE" : "MALE";
        
        Integer minAge = filter.getMinAge();
        Integer maxAge = filter.getMaxAge();
        
        String regionPartition = filter.getRegionPartition();
        
        log.debug("매칭 실행 - 회원 ID: {}, 성별: {}, 이성: {}, 나이 범위: {}-{}, 지역: {}", 
                memberProfile.getId(), memberProfile.getDecryptedGender(), oppositeGender, minAge, maxAge, regionPartition);
        
        List<MatchingFilter> potentialMatchFilters;
        
        if (regionPartition != null) {
            log.debug("지역 및 성별 파티션 우선순위를 사용한 매칭: {}", regionPartition);
            potentialMatchFilters = matchingFilterRepository.findByMemberProfileGenderAndRegionPriority(oppositeGender, regionPartition);
        } else {
            log.debug("성별 파티션만 사용한 매칭");
            potentialMatchFilters = matchingFilterRepository.findByMemberProfileGender(oppositeGender);
        }
        
        log.debug("잠재적 매칭 필터 수: {}", potentialMatchFilters.size());
        
        List<MemberProfile> potentialMatches = potentialMatchFilters.stream()
                .map(MatchingFilter::getMemberProfile)
                .filter(profile -> !profile.getId().equals(memberProfile.getId()))
                .filter(profile -> {
                    Integer age = profile.getAge();
                    return age != null && (minAge == null || age >= minAge) && (maxAge == null || age <= maxAge);
                })
                .collect(Collectors.toList());
        
        log.debug("나이 필터링 후 잠재적 매칭 수: {}", potentialMatches.size());
        
        List<Map.Entry<MemberProfile, Integer>> scoredMatches = potentialMatches.stream()
                .map(profile -> new AbstractMap.SimpleEntry<>(
                        profile, calculateCompatibility(memberProfile, profile)))
                .sorted(Map.Entry.<MemberProfile, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
        
        List<MemberProfile> result = scoredMatches.stream()
                .limit(MAX_MATCHES)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        log.debug("최종 매칭 결과 수: {}", result.size());
        return result;
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public int calculateCompatibility(MemberProfile sourceProfile, MemberProfile targetProfile) {
        MatchingFilter filter = matchingFilterRepository.findByMemberProfile(sourceProfile)
                .orElse(null);
        
        if (filter == null || filter.getIdealFaceFeaturesBitmask() == null) {
            return 50;
        }
        
        BigInteger idealFeatures = filter.getIdealFaceFeaturesBitmask();
        BigInteger targetFeatures = targetProfile.getFaceFeaturesBitmask();
        
        BigInteger matchingBits = idealFeatures.and(targetFeatures);
        
        int totalIdealFeatures = idealFeatures.bitCount();
        int matchingFeatures = matchingBits.bitCount();
        
        if (totalIdealFeatures == 0) {
            return 50;
        }
        
        return (int) (((double) matchingFeatures / totalIdealFeatures) * 100);
    }
}
