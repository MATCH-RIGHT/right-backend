package com.example.rightbackend.docs;

import com.example.rightbackend.global.DummyGenerator;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.MemberSuccess;
import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.member.controller.dto.request.CheckIdRequest;
import com.example.rightbackend.member.controller.dto.request.ResetPasswordRequest;
import com.example.rightbackend.member.controller.dto.request.SearchIdRequest;
import com.example.rightbackend.member.controller.dto.request.SignUpRequest;
import com.example.rightbackend.member.controller.dto.request.UpdateProfileRequest;
import com.example.rightbackend.member.controller.dto.response.InterestListResponse;
import com.example.rightbackend.member.controller.dto.response.LocationListResponse;
import com.example.rightbackend.member.controller.dto.response.MemberPageResponse;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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

        this.mockMvc.perform(post("/api/member/sign-up")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("member-signUp",
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("providerId").description("아이디"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("phoneNumber").description("전화번호"),
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("gender").description("성별"),
                                fieldWithPath("birthday").description("생년월일(YYYY-MM-DD)"),
                                fieldWithPath("location").description("지역 ID"),
                                fieldWithPath("height").description("키 (cm)"),
                                fieldWithPath("bodyType").description("체형 ID"),
                                fieldWithPath("job").description("직업 ID"),
                                fieldWithPath("interests").description("관심사 ID 리스트"),
                                fieldWithPath("introduction").description("자기소개")
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

        this.mockMvc.perform(post("/api/member/check-id")
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

        this.mockMvc.perform(get("/api/member/get-profile")
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
                                fieldWithPath("result.height").description("키 (cm)"),
                                fieldWithPath("result.bodyType").description("체형"),
                                fieldWithPath("result.job").description("직업"),
                                fieldWithPath("result.interests").description("관심사 목록"),
                                fieldWithPath("result.interests[].id").description("관심사 ID"),
                                fieldWithPath("result.interests[].name").description("관심사 이름"),
                                fieldWithPath("result.introduction").description("자기소개")
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

        this.mockMvc.perform(post("/api/member/search-id")
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

        this.mockMvc.perform(post("/api/member/reset-password")
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
    
    @Test
    @DisplayName("API - 관심사 목록 조회")
    void getAllInterests() throws Exception {
        try {
            List<InterestListResponse.InterestDto> interestDtos = new ArrayList<>();
            interestDtos.add(new InterestListResponse.InterestDto(1L, null, "독서"));
            interestDtos.add(new InterestListResponse.InterestDto(2L, null, "여행"));
            interestDtos.add(new InterestListResponse.InterestDto(3L, null, "영화"));
            
            InterestListResponse interestListResponse = new InterestListResponse(interestDtos);
            SuccessResponse<InterestListResponse> response = SuccessResponse.of(
                    MemberSuccess.GET_INTERESTS_SUCCESS, 
                    interestListResponse
            );
            
            doReturn(response).when(memberController).getAllInterests();
            
            this.mockMvc.perform(get("/api/member/interests")
                            .contentType("application/json"))
                    .andExpect(status().isOk())
                    .andDo(document("member-get-interests",
                            responseFields(
                                    fieldWithPath("code").description("성공 코드"),
                                    fieldWithPath("result.interests").description("관심사 목록"),
                                    fieldWithPath("result.interests[].id").description("관심사 ID"),
                                    fieldWithPath("result.interests[].icon").description("관심사 아이콘"),
                                    fieldWithPath("result.interests[].label").description("관심사 이름")
                            )
                    ));
        } catch (Exception e) {
            System.out.println("관심사 목록 조회 문서화 테스트 실패: " + e.getMessage());
            throw e;
        }
    }
    

    
    @Test
    @DisplayName("API - 지역 목록 조회")
    void getAllLocations() throws Exception {
        try {
            // 테스트용 지역 데이터 생성
            List<LocationListResponse.LocationDto> locationDtos = new ArrayList<>();
            locationDtos.add(new LocationListResponse.LocationDto(1L, "서울"));
            locationDtos.add(new LocationListResponse.LocationDto(2L, "경기"));
            locationDtos.add(new LocationListResponse.LocationDto(3L, "부산"));
            
            LocationListResponse locationListResponse = new LocationListResponse(locationDtos);
            SuccessResponse<LocationListResponse> response = SuccessResponse.of(
                    MemberSuccess.GET_LOCATIONS_SUCCESS, 
                    locationListResponse
            );
            
            doReturn(response).when(memberController).getAllLocations();
            
            this.mockMvc.perform(get("/api/member/locations")
                            .contentType("application/json"))
                    .andExpect(status().isOk())
                    .andDo(document("member-get-locations",
                            responseFields(
                                    fieldWithPath("code").description("성공 코드"),
                                    fieldWithPath("result.locations").description("지역 목록"),
                                    fieldWithPath("result.locations[].id").description("지역 ID"),
                                    fieldWithPath("result.locations[].name").description("지역 이름")
                            )
                    ));
        } catch (Exception e) {
            System.out.println("지역 목록 조회 문서화 테스트 실패: " + e.getMessage());
            throw e;
        }
    }
    
    @Test
    @DisplayName("API - 회원 정보 수정")
    void updateProfile() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "새로운 닉네임",
                null,
                null,
                1,
                null,
                null,
                null,
                List.of(1L, 2L, 3L),
                "새로운 자기소개"
        );
        
        String message = MemberSuccess.UPDATE_PROFILE_SUCCESS.getMessage();
        SuccessResponse<String> response = SuccessResponse.of(MemberSuccess.UPDATE_PROFILE_SUCCESS, message);
        
        doReturn(response).when(memberController).updateProfile(any(LoginMember.class), any(UpdateProfileRequest.class));
        
        this.mockMvc.perform(put("/api/member/update-profile")
                        .header("Authorization", GIVEN_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("member-update-profile",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("닉네임 (선택)").optional(),
                                fieldWithPath("gender").description("성별 (선택)").optional(),
                                fieldWithPath("birthday").description("생년월일(YYYYMMDD) (선택)").optional(),
                                fieldWithPath("location").description("지역 ID (선택)").optional(),
                                fieldWithPath("height").description("키 (cm) (선택)").optional(),
                                fieldWithPath("bodyType").description("체형 ID (선택)").optional(),
                                fieldWithPath("job").description("직업 ID (선택)").optional(),
                                fieldWithPath("interests").description("관심사 ID 리스트 (선택)").optional(),
                                fieldWithPath("introduction").description("자기소개 (선택)").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("회원 정보 수정 결과")
                        )
                ));
    }

    private SignUpRequest createSampleSignUpRequest() {
        return new SignUpRequest(
                "홍길동",                // name
                "google-1234",          // providerId
                "securePassword",       // password
                "01012345678",          // phoneNumber
                "길동",                 // nickname
                "남성",                 // gender
                "1990-01-01",             // birthday
                1,                  // location
                180,                  // height
                1,             // bodyType
                1,               // job
                List.of(1L, 2L, 3L), // interests
                "자기소개글 예시"        // introduction
        );
    }

    private MemberPageResponse createSimpleMemberPageResponse() {
        List<MemberPageResponse.InterestDto> interests = new ArrayList<>();
        interests.add(new MemberPageResponse.InterestDto(1L, "독서"));
        interests.add(new MemberPageResponse.InterestDto(2L, "운동"));
        interests.add(new MemberPageResponse.InterestDto(3L, "여행"));
        
        return new MemberPageResponse(
                "홍길동",                   // name
                "길동이",                   // nickname
                "서울",                      // locationName
                180,                     // height
                "보통",                    // bodyType
                "개발자",                  // job
                interests,                 // interests with id
                "안녕하세요, 홍길동입니다."   // introduction
        );
    }
}