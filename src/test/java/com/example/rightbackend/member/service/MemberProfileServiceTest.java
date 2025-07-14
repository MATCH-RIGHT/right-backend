package com.example.rightbackend.member.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.global.BaseIntegrationTest;
import com.example.rightbackend.global.DummyGenerator;
import com.example.rightbackend.global.config.loader.InterestDataLoader;
import com.example.rightbackend.global.config.loader.LocationDataLoader;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.member.controller.dto.request.CheckIdRequest;
import com.example.rightbackend.member.controller.dto.request.ResetPasswordRequest;
import com.example.rightbackend.member.controller.dto.request.SearchIdRequest;
import com.example.rightbackend.member.controller.dto.request.SignUpRequest;
import com.example.rightbackend.member.controller.dto.request.UpdateProfileRequest;
import com.example.rightbackend.member.controller.dto.response.MemberResponse;
import com.example.rightbackend.member.domain.Interest;
import com.example.rightbackend.member.domain.Location;
import com.example.rightbackend.member.domain.repository.InterestRepository;
import com.example.rightbackend.member.domain.repository.LocationRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class MemberProfileServiceTest extends BaseIntegrationTest {

    @Autowired MemberProfileService memberProfileService;
    @Autowired DummyGenerator dummyGenerator;
    @Autowired MemberRepository memberRepository;
    @Autowired InterestRepository interestRepository;
    @Autowired InterestDataLoader interestDataLoader;
    @Autowired LocationRepository locationRepository;
    @Autowired LocationDataLoader locationDataLoader;

    @BeforeEach
    void setUp() {
        Member member = dummyGenerator.generateSingleMember();
    }

    @Test
    @DisplayName("회원가입")
    void signUpTest() {
        // Given
        String exceptedMessage = MemberResponse.SIGN_UP_SUCCESS.getMessage();
        SignUpRequest signUpRequest = createSignUpRequest();

        // When
        String result = memberProfileService.signUp(signUpRequest);

        // Then
        Assertions.assertEquals(exceptedMessage, result);
    }

    @Test
    @DisplayName("중복 ID 체크 성공")
    void checkDuplicateIdTest_Success() {
        // Given
        CheckIdRequest checkIdRequest = new CheckIdRequest("validuser123");

        // When
        String result = memberProfileService.checkDuplicateId(checkIdRequest);

        // Then
        Assertions.assertEquals(MemberResponse.AVAILABLE_ID.getMessage(), result);
    }

    @Test
    @DisplayName("아이디 찾기 성공")
    void searchIdTest_Success() {
        // Given
        Member member = dummyGenerator.generateSingleMember();
        SearchIdRequest searchIdRequest = new SearchIdRequest(
                member.getName(),
                member.getPhoneNumber()
        );

        // When
        String result = memberProfileService.searchId(searchIdRequest);

        // Then
        Assertions.assertEquals(member.getProviderId(), result);
    }

    @Test
    @DisplayName("아이디 찾기 실패 - 존재하지 않는 회원")
    void searchIdTest_Fail() {
        // Given
        SearchIdRequest searchIdRequest = new SearchIdRequest(
                "존재하지 않는 이름",
                "010-9999-9999"
        );

        // When & Then
        Assertions.assertThrows(RestApiException.class, () -> {
            memberProfileService.searchId(searchIdRequest);
        });
    }

    @Test
    @DisplayName("비밀번호 재설정 성공")
    void resetPasswordTest_Success() {
        // Given
        Member member = dummyGenerator.generateSingleMember();
        String newPassword = "NewPass123!";
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(
                member.getName(),
                member.getPhoneNumber(),
                newPassword
        );

        // When
        String result = memberProfileService.resetPassword(resetPasswordRequest);

        // Then
        Assertions.assertEquals(MemberResponse.PASSWORD_CHANGE_SUCCESS.getMessage(), result);
    }

    @Test
    @DisplayName("비밀번호 재설정 실패 - 존재하지 않는 회원")
    void resetPasswordTest_Fail() {
        // Given
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(
                "존재하지 않는 이름",
                "010-9999-9999",
                "NewPass123!"
        );

        // When & Then
        Assertions.assertThrows(RestApiException.class, () -> {
            memberProfileService.resetPassword(resetPasswordRequest);
        });
    }

    @Test
    @DisplayName("관심사 목록 조회")
    void getAllInterestsTest() {
        // Given
        interestDataLoader.loadInterestData();

        // When
        List<Interest> result = memberProfileService.getAllInterests();

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertTrue(result.stream().anyMatch(i -> i.getName().equals("독서")));
        Assertions.assertTrue(result.stream().anyMatch(i -> i.getName().equals("여행")));
        Assertions.assertTrue(result.stream().anyMatch(i -> i.getName().equals("강아지")));
        Assertions.assertTrue(result.size() >= 20);
        
        // ID 값 확인
        Assertions.assertNotNull(result.get(0).getId());
        Assertions.assertTrue(result.stream().allMatch(i -> i.getId() != null));
    }
    
    @Test
    @DisplayName("지역 목록 조회")
    void getAllLocationsTest() {
        // Given
        locationDataLoader.loadLocationData();

        // When
        List<Location> result = memberProfileService.getAllLocations();

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertTrue(result.stream().anyMatch(r -> r.getName().equals("서울특별시 강남구")));
        Assertions.assertTrue(result.stream().anyMatch(r -> r.getName().equals("부산광역시 해운대구")));
        Assertions.assertTrue(result.stream().anyMatch(r -> r.getName().equals("인천광역시 중구")));
        Assertions.assertTrue(result.size() >= 200);
    }
    
    @Test
    @DisplayName("회원 정보 수정")
    @Transactional
    void updateProfileTest() {
        // Given
        Member member = dummyGenerator.generateSingleMember();
        LoginMember loginMember = new LoginMember(member.getId(), member.getRole());
        
        String newNickname = "새로운 닉네임";
        String newLocationName = "부산";
        List<String> newInterests = List.of("독서", "여행", "게임");
        String newMyself = "새로운 자기소개";
        
        UpdateProfileRequest request = new UpdateProfileRequest(
                newNickname,
                null,
                null,
                newLocationName,
                null,
                null,
                null,
                newInterests,
                newMyself
        );
        
        // When
        String result = memberProfileService.updateProfile(loginMember, request);
        
        // Then
        Assertions.assertEquals(MemberResponse.PROFILE_UPDATE_SUCCESS.getMessage(), result);
        
        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
        Assertions.assertEquals(TextEncoder.encrypt(newNickname), updatedMember.getMemberProfile().getNickname());
        Assertions.assertEquals(newMyself, updatedMember.getMemberProfile().getMyself());
        
        List<String> updatedInterests = updatedMember.getMemberProfile().getMemberProfileToInterests().stream()
                .map(link -> link.getInterest().getName())
                .toList();
        Assertions.assertEquals(newInterests.size(), updatedInterests.size());
        Assertions.assertTrue(updatedInterests.containsAll(newInterests));
        
        String updatedLocation = null;
        if (!updatedMember.getMemberProfile().getMemberProfileToLocations().isEmpty()) {
            updatedLocation = updatedMember.getMemberProfile().getMemberProfileToLocations().get(0).getLocation().getName();
        }
        Assertions.assertEquals(newLocationName, updatedLocation);
    }

    private SignUpRequest createSignUpRequest() {
        SignUpRequest signUpRequest = new SignUpRequest(
                DummyGenerator.GIVEN_NAME,
                DummyGenerator.GIVEN_PROVIDER,
                DummyGenerator.GIVEN_PROVIDER_ID,
                DummyGenerator.GIVEN_PASSWORD,
                DummyGenerator.GIVEN_PHONE_NUMBER,
                DummyGenerator.GIVEN_NICKNAME,
                DummyGenerator.GIVEN_GENDER,
                DummyGenerator.GIVEN_BIRTHDAY,
                DummyGenerator.GIVEN_LOCATION_NAME,
                DummyGenerator.GIVEN_HEIGHT,
                DummyGenerator.GIVEN_BODY_TYPE,
                DummyGenerator.GIVEN_JOB,
                DummyGenerator.GIVEN_INTERESTS,
                DummyGenerator.GIVEN_MYSELF);
        return signUpRequest;
    }
}