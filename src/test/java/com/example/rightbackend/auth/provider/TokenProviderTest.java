package com.example.rightbackend.auth.provider;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.MemberRole;
import com.example.rightbackend.auth.service.TokenProvider;
import com.example.rightbackend.global.BaseIntegrationTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TokenProviderTest extends BaseIntegrationTest {

    @Autowired
    private TokenProvider tokenProvider;

    private final LoginMember givenLoginMember = new LoginMember(1L, MemberRole.MEMBER);
    private final Long givenTokenId = 1L;

    @Test
    void generateAccessTokenTest() {
        // When
        String accessToken = tokenProvider.generateAccessToken(givenLoginMember);

        // Then
        assertNotNull(accessToken);
    }

    @Test
    void generateRefreshTokenTest() {
        // When
        String refreshToken = tokenProvider.generateRefreshToken(givenLoginMember, givenTokenId);

        // Then
        assertNotNull(refreshToken);
    }

    @Test
    void getLoginMemberFromAccessTokenTest() {
        // Given
        String accessToken = tokenProvider.generateAccessToken(givenLoginMember);

        // When
        LoginMember result = tokenProvider.getLoginFromToken(accessToken);

        // Then
        assertEquals(givenLoginMember, result);
    }
}