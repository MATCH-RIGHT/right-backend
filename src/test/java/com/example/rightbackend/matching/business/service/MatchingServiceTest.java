package com.example.rightbackend.matching.business.service;

import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.matching.business.domain.MatchingResult;
import com.example.rightbackend.matching.business.domain.repository.MatchingResultRepository;
import com.example.rightbackend.matching.business.type.MatchingType;
import com.example.rightbackend.member.domain.MemberProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private MatchingResultRepository matchingResultRepository;

    @Mock
    private MatchingType freeMatchingType;

    @Mock
    private MatchingType premiumMatchingType;

    @InjectMocks
    private MatchingService matchingService;

    private Map<String, MatchingType> matchingTypes;
    private MemberProfile sourceMemberProfile;
    private MemberProfile targetMemberProfile;
    private MatchingResult freeMatchingResult;
    private MatchingResult premiumMatchingResult;

    @BeforeEach
    void setUp() {
        matchingTypes = new HashMap<>();
        matchingTypes.put("FREE", freeMatchingType);
        matchingTypes.put("PREMIUM", premiumMatchingType);

        try {
            java.lang.reflect.Field field = MatchingService.class.getDeclaredField("matchingTypes");
            field.setAccessible(true);
            field.set(matchingService, matchingTypes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            sourceMemberProfile = createMemberProfileWithReflection(1L);
            targetMemberProfile = createMemberProfileWithReflection(2L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        freeMatchingResult = MatchingResult.of(sourceMemberProfile, targetMemberProfile, 80, "FREE");
        freeMatchingResult.setId(1L);
        
        premiumMatchingResult = MatchingResult.of(sourceMemberProfile, targetMemberProfile, 90, "PREMIUM");
        premiumMatchingResult.setId(2L);
    }
    
    @Test
    @DisplayName("무료 매칭 실행 - 성공")
    void executeMatching_FreeSuccess() {
        // given
        String matchingTypeName = "free";
        List<MemberProfile> matchedProfiles = List.of(targetMemberProfile);
        
        when(freeMatchingType.executeMatching(any(MemberProfile.class))).thenReturn(matchedProfiles);
        when(freeMatchingType.calculateCompatibility(any(MemberProfile.class), any(MemberProfile.class))).thenReturn(80);
        when(freeMatchingType.getTypeName()).thenReturn("FREE");
        when(matchingResultRepository.findActiveMatchingBetweenProfiles(any(MemberProfile.class), any(MemberProfile.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(matchingResultRepository.save(any(MatchingResult.class))).thenAnswer(invocation -> {
            MatchingResult result = invocation.getArgument(0);
            result.setId(1L);
            return result;
        });

        // when
        List<MatchingResult> results = matchingService.executeMatching(sourceMemberProfile, matchingTypeName);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getSourceMemberProfile()).isEqualTo(sourceMemberProfile);
        assertThat(results.get(0).getTargetMemberProfile()).isEqualTo(targetMemberProfile);
        assertThat(results.get(0).getCompatibilityScore()).isEqualTo(80);
        assertThat(results.get(0).getMatchingType()).isEqualTo("FREE");
        
        verify(freeMatchingType).executeMatching(any(MemberProfile.class));
        verify(freeMatchingType).calculateCompatibility(any(MemberProfile.class), any(MemberProfile.class));
        verify(matchingResultRepository).save(any(MatchingResult.class));
    }
    
    @Test
    @DisplayName("프리미엄 매칭 실행 - 성공")
    void executeMatching_PremiumSuccess() {
        // given
        String matchingTypeName = "premium";
        List<MemberProfile> matchedProfiles = List.of(targetMemberProfile);
        
        when(premiumMatchingType.executeMatching(any(MemberProfile.class))).thenReturn(matchedProfiles);
        when(premiumMatchingType.calculateCompatibility(any(MemberProfile.class), any(MemberProfile.class))).thenReturn(90);
        when(premiumMatchingType.getTypeName()).thenReturn("PREMIUM");
        when(matchingResultRepository.findActiveMatchingBetweenProfiles(any(MemberProfile.class), any(MemberProfile.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(matchingResultRepository.save(any(MatchingResult.class))).thenAnswer(invocation -> {
            MatchingResult result = invocation.getArgument(0);
            result.setId(2L);
            return result;
        });

        // when
        List<MatchingResult> results = matchingService.executeMatching(sourceMemberProfile, matchingTypeName);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getSourceMemberProfile()).isEqualTo(sourceMemberProfile);
        assertThat(results.get(0).getTargetMemberProfile()).isEqualTo(targetMemberProfile);
        assertThat(results.get(0).getCompatibilityScore()).isEqualTo(90);
        assertThat(results.get(0).getMatchingType()).isEqualTo("PREMIUM");
        
        verify(premiumMatchingType).executeMatching(any(MemberProfile.class));
        verify(premiumMatchingType).calculateCompatibility(any(MemberProfile.class), any(MemberProfile.class));
        verify(matchingResultRepository).save(any(MatchingResult.class));
    }

    @Test
    @DisplayName("무료 매칭 실행 - 이미 활성화된 매칭이 있는 경우")
    void executeMatching_FreeWithExistingMatch() {
        // given
        String matchingTypeName = "free";
        List<MemberProfile> matchedProfiles = List.of(targetMemberProfile);
        
        when(freeMatchingType.executeMatching(any(MemberProfile.class))).thenReturn(matchedProfiles);
        when(matchingResultRepository.findActiveMatchingBetweenProfiles(any(MemberProfile.class), any(MemberProfile.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(freeMatchingResult));

        // when
        List<MatchingResult> results = matchingService.executeMatching(sourceMemberProfile, matchingTypeName);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(freeMatchingResult);
        
        verify(freeMatchingType).executeMatching(any(MemberProfile.class));
        verify(matchingResultRepository, never()).save(any(MatchingResult.class));
    }
    
    @Test
    @DisplayName("프리미엄 매칭 실행 - 이미 활성화된 매칭이 있는 경우")
    void executeMatching_PremiumWithExistingMatch() {
        // given
        String matchingTypeName = "premium";
        List<MemberProfile> matchedProfiles = List.of(targetMemberProfile);
        
        when(premiumMatchingType.executeMatching(any(MemberProfile.class))).thenReturn(matchedProfiles);
        when(matchingResultRepository.findActiveMatchingBetweenProfiles(any(MemberProfile.class), any(MemberProfile.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(premiumMatchingResult));

        // when
        List<MatchingResult> results = matchingService.executeMatching(sourceMemberProfile, matchingTypeName);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(premiumMatchingResult);
        
        verify(premiumMatchingType).executeMatching(any(MemberProfile.class));
        verify(matchingResultRepository, never()).save(any(MatchingResult.class));
    }

    @Test
    @DisplayName("매칭 실행 - 지원하지 않는 매칭 타입")
    void executeMatching_UnsupportedType() {
        // given
        String matchingTypeName = "UNSUPPORTED";

        // when & then
        assertThatThrownBy(() -> matchingService.executeMatching(sourceMemberProfile, matchingTypeName))
                .isInstanceOf(RestApiException.class)
                .hasMessageContaining("지원하지 않는 매칭 타입");
    }
    
    @Test
    @DisplayName("좋아요 처리 - 소스 회원 좋아요")
    void like_SourceMemberLike() {
        // given
        Long matchingResultId = 1L;
        
        when(matchingResultRepository.findById(matchingResultId)).thenReturn(Optional.of(freeMatchingResult));
        when(matchingResultRepository.save(any(MatchingResult.class))).thenReturn(freeMatchingResult);

        // when
        matchingService.like(matchingResultId, sourceMemberProfile);

        // then
        assertThat(freeMatchingResult.isSourceLiked()).isTrue();
        assertThat(freeMatchingResult.isTargetLiked()).isFalse();
        assertThat(freeMatchingResult.isMatched()).isFalse();
        
        verify(matchingResultRepository).findById(matchingResultId);
        verify(matchingResultRepository).save(freeMatchingResult);
    }
    
    @Test
    @DisplayName("좋아요 처리 - 타겟 회원 좋아요")
    void like_TargetMemberLike() {
        // given
        Long matchingResultId = 1L;
        
        when(matchingResultRepository.findById(matchingResultId)).thenReturn(Optional.of(freeMatchingResult));
        when(matchingResultRepository.save(any(MatchingResult.class))).thenReturn(freeMatchingResult);

        // when
        matchingService.like(matchingResultId, targetMemberProfile);

        // then
        assertThat(freeMatchingResult.isSourceLiked()).isFalse();
        assertThat(freeMatchingResult.isTargetLiked()).isTrue();
        assertThat(freeMatchingResult.isMatched()).isFalse();
        
        verify(matchingResultRepository).findById(matchingResultId);
        verify(matchingResultRepository).save(freeMatchingResult);
    }
    
    @Test
    @DisplayName("좋아요 처리 - 양쪽 모두 좋아요하여 매칭 성립")
    void like_BothLikeAndMatch() {
        // given
        Long matchingResultId = 1L;
        freeMatchingResult.sourceLike();
        
        when(matchingResultRepository.findById(matchingResultId)).thenReturn(Optional.of(freeMatchingResult));
        when(matchingResultRepository.save(any(MatchingResult.class))).thenReturn(freeMatchingResult);

        // when
        matchingService.like(matchingResultId, targetMemberProfile);

        // then
        assertThat(freeMatchingResult.isSourceLiked()).isTrue();
        assertThat(freeMatchingResult.isTargetLiked()).isTrue();
        assertThat(freeMatchingResult.isMatched()).isTrue();
        
        verify(matchingResultRepository).findById(matchingResultId);
        verify(matchingResultRepository).save(freeMatchingResult);
    }
    
    @Test
    @DisplayName("좋아요 처리 - 존재하지 않는 매칭 결과")
    void like_NonExistentMatchingResult() {
        // given
        Long matchingResultId = 999L;
        
        when(matchingResultRepository.findById(matchingResultId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> matchingService.like(matchingResultId, sourceMemberProfile))
                .isInstanceOf(RestApiException.class)
                .hasMessageContaining("존재하지 않는 매칭");
    }
    
    @Test
    @DisplayName("좋아요 처리 - 만료된 매칭")
    void like_ExpiredMatching() {
        // given
        Long matchingResultId = 1L;
        freeMatchingResult.setExpiresAt(LocalDateTime.now().minusDays(1)); // 만료된 매칭
        
        when(matchingResultRepository.findById(matchingResultId)).thenReturn(Optional.of(freeMatchingResult));

        // when & then
        assertThatThrownBy(() -> matchingService.like(matchingResultId, sourceMemberProfile))
                .isInstanceOf(RestApiException.class)
                .hasMessageContaining("만료된 매칭");
    }
    
    @Test
    @DisplayName("좋아요 처리 - 매칭에 참여하지 않은 회원")
    void like_NonParticipatingMember() {
        // given
        Long matchingResultId = 1L;
        MemberProfile otherMemberProfile;
        try {
            otherMemberProfile = createMemberProfileWithReflection(3L);
            
            when(matchingResultRepository.findById(matchingResultId)).thenReturn(Optional.of(freeMatchingResult));

            // when & then
            assertThatThrownBy(() -> matchingService.like(matchingResultId, otherMemberProfile))
                    .isInstanceOf(RestApiException.class)
                    .hasMessageContaining("해당 매칭에 참여하지 않은 사용자");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @DisplayName("활성화된 매칭 결과 조회")
    void getActiveMatchings() {
        // given
        List<MatchingResult> activeMatchings = List.of(freeMatchingResult);
        
        when(matchingResultRepository.findActiveMatchingsByMemberProfile(eq(sourceMemberProfile), any(LocalDateTime.class)))
                .thenReturn(activeMatchings);

        // when
        List<MatchingResult> results = matchingService.getActiveMatchings(sourceMemberProfile);

        // then
        assertThat(results).hasSize(1);
        assertThat(results).containsExactly(freeMatchingResult);
        
        verify(matchingResultRepository).findActiveMatchingsByMemberProfile(eq(sourceMemberProfile), any(LocalDateTime.class));
    }
    
    @Test
    @DisplayName("매칭된 결과 조회")
    void getMatchedResults() {
        // given
        freeMatchingResult.sourceLike();
        freeMatchingResult.targetLike(); // 양쪽 다 좋아요 해서 매칭 성공
        List<MatchingResult> matchedResults = List.of(freeMatchingResult);
        
        when(matchingResultRepository.findMatchedResults(sourceMemberProfile)).thenReturn(matchedResults);

        // when
        List<MatchingResult> results = matchingService.getMatchedResults(sourceMemberProfile);

        // then
        assertThat(results).hasSize(1);
        assertThat(results).containsExactly(freeMatchingResult);
        assertThat(results.get(0).isMatched()).isTrue();
        
        verify(matchingResultRepository).findMatchedResults(sourceMemberProfile);
    }
    
    @Test
    @DisplayName("만료된 매칭 결과 삭제")
    void cleanupExpiredMatchings() {
        // given
        List<MatchingResult> expiredMatchings = List.of(freeMatchingResult);
        
        when(matchingResultRepository.findByExpiresAtLessThanAndMatchedFalse(any(LocalDateTime.class)))
                .thenReturn(expiredMatchings);
        doNothing().when(matchingResultRepository).deleteAll(expiredMatchings);

        // when
        int deletedCount = matchingService.cleanupExpiredMatchings();

        // then
        assertThat(deletedCount).isEqualTo(1);
        
        verify(matchingResultRepository).findByExpiresAtLessThanAndMatchedFalse(any(LocalDateTime.class));
        verify(matchingResultRepository).deleteAll(expiredMatchings);
    }
    
    private MemberProfile createMemberProfileWithReflection(Long id) throws Exception {
        Constructor<MemberProfile> constructor = MemberProfile.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        MemberProfile memberProfile = constructor.newInstance();
        
        // ID 설정
        java.lang.reflect.Field idField = MemberProfile.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(memberProfile, id);
        
        return memberProfile;
    }


}