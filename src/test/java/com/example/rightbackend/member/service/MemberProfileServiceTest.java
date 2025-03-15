package com.example.rightbackend.member.service;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.global.BaseIntegrationTest;
import com.example.rightbackend.global.DummyGenerator;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.member.controller.dto.request.CheckIdRequest;
import com.example.rightbackend.member.controller.dto.request.ResetPasswordRequest;
import com.example.rightbackend.member.controller.dto.request.SearchIdRequest;
import com.example.rightbackend.member.controller.dto.request.SignUpRequest;
import com.example.rightbackend.member.controller.dto.response.MemberResponse;
import com.example.rightbackend.member.domain.repository.InterestRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class MemberProfileServiceTest extends BaseIntegrationTest {

    @Autowired MemberProfileService memberProfileService;
    @Autowired DummyGenerator dummyGenerator;
    @Autowired MemberRepository memberRepository;
    @Autowired InterestRepository interestRepository;

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
        CheckIdRequest checkIdRequest = new CheckIdRequest("new_id_123");

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
                "존재하지 않는 전화번호"
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
        String newPassword = "newPassword123";
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
                "존재하지 않는 전화번호",
                "newPassword123"
        );

        // When & Then
        Assertions.assertThrows(RestApiException.class, () -> {
            memberProfileService.resetPassword(resetPasswordRequest);
        });
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
                DummyGenerator.GIVEN_ADDRESS,
                DummyGenerator.GIVEN_HEIGHT,
                DummyGenerator.GIVEN_BODY_TYPE,
                DummyGenerator.GIVEN_JOB,
                DummyGenerator.GIVEN_INTERESTS,
                DummyGenerator.GIVEN_MYSELF);
        return signUpRequest;
    }
}