package com.example.rightbackend.matching.filter.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.MemberRole;
import com.example.rightbackend.auth.domain.repository.MemberRepository;

import com.example.rightbackend.global.BaseIntegrationTest;
import com.example.rightbackend.global.DummyGenerator;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.MatchingFilterError;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.matching.filter.controller.dto.request.MatchingFilterRequest;
import com.example.rightbackend.matching.filter.domain.MatchingFilter;
import com.example.rightbackend.matching.filter.domain.Region;
import com.example.rightbackend.matching.filter.domain.repository.MatchingFilterRepository;
import com.example.rightbackend.matching.filter.domain.repository.RegionRepository;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.domain.repository.MemberProfileRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MatchingFilterServiceTest extends BaseIntegrationTest {

    @Autowired
    private MatchingFilterService matchingFilterService;
    
    @Autowired
    private MatchingFilterRepository matchingFilterRepository;
    
    @Autowired
    private RegionRepository regionRepository;
    
    @Autowired
    private DummyGenerator dummyGenerator;
    
    private MemberProfile memberProfile;
    private Region region;
    private Member member;
    private LoginMember loginMember;
    
    @BeforeEach
    public void setUpTest() {
        member = dummyGenerator.generateSingleMember();
        memberProfile = member.getMemberProfile();
        loginMember = new LoginMember(member.getId(), MemberRole.MEMBER);
        
        List<Region> regions = regionRepository.findAll();
        if (!regions.isEmpty()) {
            region = regions.get(0);
        } else {
            region = regionRepository.save(Region.of("수도권", "CAPITAL"));
        }
    }
    
    @Test
    @DisplayName("매칭 필터 조회 - 성공")
    void getMatchingFilterTest_Success() {
        MatchingFilterRepository mockRepo = Mockito.mock(MatchingFilterRepository.class);
        MemberRepository mockMemberRepo = Mockito.mock(MemberRepository.class);
        MemberProfileRepository mockProfileRepo = Mockito.mock(MemberProfileRepository.class);
        RegionRepository mockRegionRepo = Mockito.mock(RegionRepository.class);
        
        MatchingFilterService testService = new MatchingFilterService(
            mockRepo, mockProfileRepo, mockRegionRepo, mockMemberRepo
        );
        
        // Given
        MatchingFilter savedFilter = createMatchingFilter();
        
        Mockito.when(mockMemberRepo.findById(loginMember.memberId())).thenReturn(Optional.of(member));
        Mockito.when(mockRepo.findByMemberProfile(memberProfile)).thenReturn(Optional.of(savedFilter));
        
        // When
        MatchingFilter result = testService.getMatchingFilter(loginMember);
        
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(savedFilter.getId(), result.getId());
        Assertions.assertEquals(savedFilter.getMinAge(), result.getMinAge());
        Assertions.assertEquals(savedFilter.getMaxAge(), result.getMaxAge());
        Assertions.assertEquals(savedFilter.getIdealFaceFeaturesBitmask(), result.getIdealFaceFeaturesBitmask());
        Assertions.assertEquals(savedFilter.getRegion().getId(), result.getRegion().getId());
    }
    
    @Test
    @DisplayName("매칭 필터 조회 - 실패 (존재하지 않는 회원 프로필)")
    void getMatchingFilterTest_Fail_NullMemberProfile() {
        // Given
        Long nonExistentMemberId = 9999L;
        LoginMember nonExistentLoginMember = new LoginMember(nonExistentMemberId, MemberRole.MEMBER);
        
        // When & Then
        RestApiException exception = Assertions.assertThrows(RestApiException.class, () -> {
            matchingFilterService.getMatchingFilter(nonExistentLoginMember);
        });
        
        Assertions.assertEquals(MemberError.NULL_MEMBER, exception.getErrorCode());
    }
    
    @Test
    @DisplayName("매칭 필터 생성 - 성공")
    void createMatchingFilterTest_Success() {
        MatchingFilterRepository mockRepo = Mockito.mock(MatchingFilterRepository.class);
        MemberRepository mockMemberRepo = Mockito.mock(MemberRepository.class);
        MemberProfileRepository mockProfileRepo = Mockito.mock(MemberProfileRepository.class);
        RegionRepository mockRegionRepo = Mockito.mock(RegionRepository.class);
        
        MatchingFilterService testService = new MatchingFilterService(
            mockRepo, mockProfileRepo, mockRegionRepo, mockMemberRepo
        );
        
        // Given
        MatchingFilterRequest request = new MatchingFilterRequest(
                20,
                30,
                Arrays.asList("OVAL", "SQUARE"),  // ROUND 대신 SQUARE 사용 (FaceShape enum 값 확인)
                region.getId()
        );
        
        Mockito.when(mockMemberRepo.findById(loginMember.memberId())).thenReturn(Optional.of(member));
        Mockito.when(mockRegionRepo.findById(region.getId())).thenReturn(Optional.of(region));
        
        Mockito.when(mockRepo.findByMemberProfile(memberProfile)).thenReturn(Optional.empty());
        Mockito.when(mockRepo.save(Mockito.any(MatchingFilter.class))).thenAnswer(invocation -> {
            MatchingFilter filter = invocation.getArgument(0);
            try {
                java.lang.reflect.Field idField = MatchingFilter.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(filter, 1L);
            } catch (Exception e) {
            }
            return filter;
        });
        
        // When
        MatchingFilter result = testService.createOrUpdateMatchingFilter(loginMember, request);
        
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(20, result.getMinAge());
        Assertions.assertEquals(30, result.getMaxAge());
        Assertions.assertEquals(region.getId(), result.getRegion().getId());
        
        Integer ovalIndex = IdealFaceFeatureUtil.getFeatureIndex("OVAL");
        Integer squareIndex = IdealFaceFeatureUtil.getFeatureIndex("SQUARE");  // ROUND 대신 SQUARE 사용
        
        if (ovalIndex != null) {
            Assertions.assertTrue(result.getIdealFaceFeaturesBitmask().testBit(ovalIndex));
        } else {
            Assertions.fail("OVAL 얼굴 특징이 IdealFaceFeatureUtil에 정의되어 있지 않습니다.");
        }
        
        if (squareIndex != null) {
            Assertions.assertTrue(result.getIdealFaceFeaturesBitmask().testBit(squareIndex));
        } else {
            Assertions.fail("SQUARE 얼굴 특징이 IdealFaceFeatureUtil에 정의되어 있지 않습니다.");
        }
    }
    
    @Test
    @DisplayName("매칭 필터 생성 - 실패 (유효하지 않은 나이 범위)")
    void createMatchingFilterTest_Fail_InvalidAgeRange() {
        // Given
        MatchingFilterRequest request = new MatchingFilterRequest(
                30,
                20,
                Collections.emptyList(),
                region.getId()
        );
        
        // When & Then
        RestApiException exception = Assertions.assertThrows(RestApiException.class, () -> {
            matchingFilterService.createOrUpdateMatchingFilter(loginMember, request);
        });
        
        Assertions.assertEquals(MatchingFilterError.INVALID_AGE_RANGE, exception.getErrorCode());
    }
    
    @Test
    @DisplayName("매칭 필터 생성 - 실패 (유효하지 않은 얼굴 특징)")
    void createMatchingFilterTest_Fail_InvalidFaceFeature() {
        // Given
        MatchingFilterRequest request = new MatchingFilterRequest(
                20,
                30,
                Arrays.asList("INVALID_FEATURE"),
                region.getId()
        );
        
        // When & Then
        RestApiException exception = Assertions.assertThrows(RestApiException.class, () -> {
            matchingFilterService.createOrUpdateMatchingFilter(loginMember, request);
        });
        
        Assertions.assertEquals(MatchingFilterError.INVALID_FACE_FEATURE, exception.getErrorCode());
    }
    
    private MatchingFilter createMatchingFilter() {
        MatchingFilter filter = MatchingFilter.of(memberProfile);
        filter.setMinAge(20);
        filter.setMaxAge(30);
        filter.setRegion(region);
        
        try {
            java.lang.reflect.Field idField = MatchingFilter.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(filter, 1L);
            return filter;
        } catch (Exception e) {
            return matchingFilterRepository.save(filter);
        }
    }
}