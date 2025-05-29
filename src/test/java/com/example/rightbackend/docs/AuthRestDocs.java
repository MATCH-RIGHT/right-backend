package com.example.rightbackend.docs;

import com.example.rightbackend.auth.controller.dto.AuthToken;
import com.example.rightbackend.auth.controller.dto.LoginRequest;
import com.example.rightbackend.global.DummyGenerator;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.AuthSuccess;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthRestDocs extends BaseRestDocsTest {
    @Test
    @DisplayName("API - 로그인")
    void login() throws Exception {
        final LoginRequest request = new LoginRequest(DummyGenerator.GIVEN_PROVIDER_ID, DummyGenerator.GIVEN_PASSWORD);
        final AuthToken authToken = new AuthToken("accessToken", "refreshToken");
        SuccessResponse<AuthToken> response = SuccessResponse.of(AuthSuccess.GENERATE_TOKEN_SUCCESS, authToken);

        doReturn(response).when(authController).login(request);

        this.mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("auth-login",
                        requestFields(
                                fieldWithPath("id").description("아이디"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result.accessToken").description("발급 된 액세스 토큰"),
                                fieldWithPath("result.refreshToken").description("발급 된 리프레쉬 토큰")
                        )
                ));
    }

    @Test
    @DisplayName("API - 로그아웃")
    void logout() throws Exception {
        final AuthToken request = new AuthToken("accessToken", "refreshToken");
        final String message = AuthSuccess.LOGOUT_SUCCESS.getMessage();
        SuccessResponse<String> response = SuccessResponse.of(AuthSuccess.LOGOUT_SUCCESS, message);

        doReturn(response).when(authController).logout(any());

        this.mockMvc.perform(post("/api/auth/logout")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("auth-logout",
                        requestFields(
                                fieldWithPath("accessToken").description("발급 된 액세스 토큰"),
                                fieldWithPath("refreshToken").description("발급 된 리프레쉬 토큰")
                        )
                ));
    }

    @Test
    @DisplayName("API - 토큰 재발급")
    void reissue() throws Exception {
        final AuthToken request = new AuthToken("expiredAccessToken", "refreshToken");
        final AuthToken result = new AuthToken("newAccessToken", "refreshToken");
        SuccessResponse<AuthToken> response = SuccessResponse.of(AuthSuccess.REISSUE_TOKEN_SUCCESS, result);

        doReturn(response).when(authController).reissue(any());

        this.mockMvc.perform(post("/api/auth/reissue")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("auth-reissue",
                        requestFields(
                                fieldWithPath("accessToken").description("만료 된 액세스 토큰"),
                                fieldWithPath("refreshToken").description("재발급을 위한 리프레쉬 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result.accessToken").description("새로 발급 된 액세스 토큰"),
                                fieldWithPath("result.refreshToken").description("기존의 리프레쉬 토큰")
                        )
                ));
    }

    @Test
    @DisplayName("API - 회원 탈퇴")
    void withdraw() throws Exception {
        String message = AuthSuccess.WITH_DRAW_SUCCESS.getMessage();
        SuccessResponse<String > response = SuccessResponse.of(AuthSuccess.WITH_DRAW_SUCCESS, message);

        doReturn(response).when(authController).withDraw(any());

        this.mockMvc.perform(patch("/api/auth/withdraw")
                .header("Authorization",  GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("auth-withdraw",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("회원 탈퇴 결과")
                        )
                ));
    }
}