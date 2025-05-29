package com.example.rightbackend.docs;

import com.example.rightbackend.global.DummyGenerator;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.MemberSuccess;
import com.example.rightbackend.member.controller.dto.request.CheckIdRequest;
import com.example.rightbackend.member.controller.dto.request.ResetPasswordRequest;
import com.example.rightbackend.member.controller.dto.request.SearchIdRequest;
import com.example.rightbackend.member.controller.dto.request.SignUpRequest;
import com.example.rightbackend.member.controller.dto.response.MemberPageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberProfileDocs extends BaseRestDocsTest {
    @Test
    @DisplayName("API - 회원가입")
    void signUp() throws Exception {
        final SignUpRequest request = createSampleSignUpRequest();
        final String message = MemberSuccess.SIGN_UP_SUCCESS.getMessage();
        SuccessResponse<String> response = SuccessResponse.of(MemberSuccess.SIGN_UP_SUCCESS, message);

        doReturn(response).when(memberController).signUp(request);

        this.mockMvc.perform(post("/member/sign-up")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("member-signUp",
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("provider").description("회원가입 한 서비스 이름"),
                                fieldWithPath("providerId").description("아이디"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("phoneNumber").description("전화번호"),
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("gender").description("성별"),
                                fieldWithPath("birthday").description("생년월일(YYYYMMDD)"),
                                fieldWithPath("address").description("주소"),
                                fieldWithPath("height").description("키"),
                                fieldWithPath("body_type").description("체형"),
                                fieldWithPath("job").description("직업"),
                                fieldWithPath("interests").description("관심사"),
                                fieldWithPath("myself").description("자기소개")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("회원가입 결과")
                        )
                ));
    }

    @Test
    @DisplayName("API - 아이디 중복 검사")
    void checkId() throws Exception {
        CheckIdRequest request = new CheckIdRequest(DummyGenerator.GIVEN_PROVIDER_ID);
        final String message = MemberSuccess.CHECK_ID_SUCCESS.getMessage();
        SuccessResponse<String> response = SuccessResponse.of(MemberSuccess.CHECK_ID_SUCCESS, message);

        doReturn(response).when(memberController).checkId(any());

        this.mockMvc.perform(post("/member/check-id")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("member-checkId",
                        requestFields(
                                fieldWithPath("providerId").description("아이디")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("아이디 중복 검사 결과")
                        )
                ));
    }

    @Test
    @DisplayName("API - 회원 정보 요청")
    void getMemberPage() throws Exception {
        MemberPageResponse result = createSimpleMemberPageResponse();
        SuccessResponse<MemberPageResponse> response = SuccessResponse.of(MemberSuccess.GET_MEMBER_PAGE_SUCCESS, result);

        doReturn(response).when(memberController).getMemberPage(any());

        this.mockMvc.perform(get("/member/member-page")
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("member-get-member-page",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("result.name").description("이름"),
                                fieldWithPath("result.nickname").description("닉네임"),
                                fieldWithPath("result.address").description("주소"),
                                fieldWithPath("result.height").description("키"),
                                fieldWithPath("result.body_type").description("체형"),
                                fieldWithPath("result.job").description("직업"),
                                fieldWithPath("result.interests").description("관심사"),
                                fieldWithPath("result.myself").description("자기소개")
                        )
                ));
    }

    @Test
    @DisplayName("API - 아이디 찾기")
    void searchId() throws Exception {
        SearchIdRequest request = new SearchIdRequest(
                DummyGenerator.GIVEN_NAME,
                DummyGenerator.GIVEN_PHONE_NUMBER
        );

        final String message = DummyGenerator.GIVEN_PROVIDER_ID; // 찾은 아이디
        SuccessResponse<String> response = SuccessResponse.of(MemberSuccess.SEARCH_ID_SUCCESS, message);

        doReturn(response).when(memberController).searchId(any());

        this.mockMvc.perform(post("/member/search-id")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("member-search-id",
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("phoneNumber").description("전화번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("찾은 아이디")
                        )
                ));
    }

    @Test
    @DisplayName("API - 비밀번호 재설정")
    void resetPassword() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest(
                DummyGenerator.GIVEN_NAME,
                DummyGenerator.GIVEN_PHONE_NUMBER,
                DummyGenerator.GIVEN_PASSWORD
        );

        final String message = MemberSuccess.CHANGE_PASSWORD_SUCCESS.getMessage();
        SuccessResponse<String> response = SuccessResponse.of(MemberSuccess.CHANGE_PASSWORD_SUCCESS, message);

        doReturn(response).when(memberController).resetPassword(any());

        this.mockMvc.perform(post("/member/reset-password")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("member-reset-password",
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("phoneNumber").description("전화번호"),
                                fieldWithPath("newPassword").description("새 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("비밀번호 재설정 결과")
                        )
                ));
    }

    private SignUpRequest createSampleSignUpRequest() {
        return new SignUpRequest(
                "홍길동",                // name
                "google",               // provider
                "google-1234",          // providerId
                "securePassword",       // password
                "01012345678",          // phoneNumber
                "길동",                 // nickname
                "남성",                 // gender
                "19900101",             // birthday
                "서울시 강남구",         // address
                "180",                  // height
                "athletic",             // body_type
                "개발자",               // job
                List.of("음악", "여행", "영화"), // interests
                "자기소개글 예시"        // myself
        );
    }

    private MemberPageResponse createSimpleMemberPageResponse() {
        return new MemberPageResponse(
                "홍길동",                   // name
                "길동이",                   // nickname
                "서울 강남구",              // address
                "180",                     // height
                "보통",                    // body_type
                "개발자",                  // job
                List.of("독서", "운동", "여행"), // interests
                "안녕하세요, 홍길동입니다."   // myself
        );
    }
}