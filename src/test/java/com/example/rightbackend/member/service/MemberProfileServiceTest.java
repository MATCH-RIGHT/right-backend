package com.example.rightbackend.member.service;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.global.BaseIntegrationTest;
import com.example.rightbackend.global.DummyGenerator;
import com.example.rightbackend.member.controller.dto.request.SignUpRequest;
import com.example.rightbackend.member.controller.dto.response.MemberResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

public class MemberProfileServiceTest extends BaseIntegrationTest {

    @Autowired MemberProfileService memberProfileService;
    @Autowired DummyGenerator dummyGenerator;

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

    private SignUpRequest createSignUpRequest() {
        SignUpRequest signUpRequest = new SignUpRequest(
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
                DummyGenerator.GIVEN_MYSELF);
        return signUpRequest;
    }
}