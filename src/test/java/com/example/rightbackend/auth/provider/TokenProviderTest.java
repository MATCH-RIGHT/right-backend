package com.example.rightbackend.auth.provider;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.MemberRole;
import com.example.rightbackend.auth.service.TokenProvider;
import com.example.rightbackend.global.BaseIntegrationTest;
import com.example.rightbackend.global.properties.JWTProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TokenProviderTest extends BaseIntegrationTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private JWTProperties jwtProperties;

    private LoginMember givenLoginMember;
    private Long givenTokenId;

    @BeforeEach
    void setUp() {
        Long memberId = 1L;
        givenTokenId = 1L;
        MemberRole role = MemberRole.MEMBER;
        givenLoginMember = new LoginMember(memberId, role);
    }

    @Test
    @DisplayName("MAKE ACCESS_TOKEN")
    void generateAccessTokenTest() {
        // When
        String accessToken = tokenProvider.generateAccessToken(givenLoginMember);

        // Then
        Assertions.assertNotNull(accessToken);
    }

    @Test
    @DisplayName("MAKE REFRESH_TOKEN")
    void generateRefreshTokenTest() {
        // When
        String refreshToken = tokenProvider.generateRefreshToken(givenLoginMember, givenTokenId);

        // Then
        Assertions.assertNotNull(refreshToken);
    }

    @Test
    @DisplayName("FROM ACCESS_TOKEN GIVEN LOGIN MEMBER INFO")
    void getLoginMemberFromAccessTokenTest() {
        // Given
        String accessToken = tokenProvider.generateAccessToken(givenLoginMember);

        // When
        LoginMember result = tokenProvider.getLoginFromToken(accessToken);

        // Then
        Assertions.assertEquals(givenLoginMember, result);
    }
}