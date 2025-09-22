package com.example.rightbackend.docs;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.global.response.SuccessResponse; 
import com.example.rightbackend.global.response.success.MatchingSuccess;
import com.example.rightbackend.image.controller.dto.response.ImageListResponse;
import com.example.rightbackend.matching.business.controller.dto.response.MatchedResponse;
import com.example.rightbackend.matching.business.controller.dto.response.MatchingResultResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MatchingDocs extends BaseRestDocsTest {

    @Test
    @DisplayName("API - 무료 매칭 실행")
    void executeFreeMatching() throws Exception {
        List<MatchingResultResponse> matchingResponses = createMatchingResponses("FREE");
        
        MatchingSuccess successCode = MatchingSuccess.EXECUTE_FREE_MATCHING_SUCCESS;
        SuccessResponse<List<MatchingResultResponse>> apiResponseBody =
                SuccessResponse.of(successCode, matchingResponses);
        
        doReturn(apiResponseBody).when(matchingController).executeFreeMatching(any(LoginMember.class));
        
        this.mockMvc.perform(post("/api/matching/free")
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("matching-free-execute",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        responseFields(
                            fieldWithPath("code").description("응답 코드"),
                            fieldWithPath("result").description("매칭 결과 목록"),
                            fieldWithPath("result[].matchingResultId").description("매칭 결과 ID"),
                            fieldWithPath("result[].memberProfile").description("매칭된 회원 프로필 정보"),
                            fieldWithPath("result[].memberProfile.id").description("회원 ID"),
                            fieldWithPath("result[].memberProfile.nickname").description("닉네임"),
                            fieldWithPath("result[].memberProfile.gender").description("성별"),
                            fieldWithPath("result[].memberProfile.age").description("나이"),
                            fieldWithPath("result[].memberProfile.location").description("지역"),
                            fieldWithPath("result[].memberProfile.height").description("키"),
                            fieldWithPath("result[].memberProfile.bodyType").description("체형"),
                            fieldWithPath("result[].memberProfile.job").description("직업"),
                            fieldWithPath("result[].memberProfile.images").description("프로필 이미지 목록"),
                            fieldWithPath("result[].memberProfile.images[].id").description("이미지 ID"),
                            fieldWithPath("result[].memberProfile.images[].fileName").description("이미지 파일명"),
                            fieldWithPath("result[].memberProfile.images[].fileUrl").description("이미지 URL"),
                            fieldWithPath("result[].memberProfile.images[].imageIndex").description("이미지 순서"),
                            fieldWithPath("result[].compatibilityScore").description("호환성 점수"),
                            fieldWithPath("result[].matchingType").description("매칭 타입"),
                            fieldWithPath("result[].createdAt").description("매칭 생성 시간"),
                            fieldWithPath("result[].expiresAt").description("매칭 만료 시간"),
                            fieldWithPath("result[].liked").description("좋아요 여부"),
                            fieldWithPath("result[].matched").description("매칭 성사 여부")
                    )
                ));
    }
    
    @Test
    @DisplayName("API - 프리미엄 매칭 실행")
    void executePremiumMatching() throws Exception {
        List<MatchingResultResponse> matchingResponses = createMatchingResponses("PREMIUM");

        MatchingSuccess successCode = MatchingSuccess.EXECUTE_PREMIUM_MATCHING_SUCCESS;
        SuccessResponse<List<MatchingResultResponse>> apiResponseBody =
                SuccessResponse.of(successCode, matchingResponses);

        doReturn(apiResponseBody).when(matchingController).executePremiumMatching(any(LoginMember.class));
        
        this.mockMvc.perform(post("/api/matching/premium")
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("matching-premium-execute",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        responseFields(
                            fieldWithPath("code").description("응답 코드"),
                            fieldWithPath("result").description("매칭 결과 목록"),
                            fieldWithPath("result[].matchingResultId").description("매칭 결과 ID"),
                            fieldWithPath("result[].memberProfile").description("매칭된 회원 프로필 정보"),
                            fieldWithPath("result[].memberProfile.id").description("회원 ID"),
                            fieldWithPath("result[].memberProfile.nickname").description("닉네임"),
                            fieldWithPath("result[].memberProfile.gender").description("성별"),
                            fieldWithPath("result[].memberProfile.age").description("나이"),
                            fieldWithPath("result[].memberProfile.location").description("지역"),
                            fieldWithPath("result[].memberProfile.height").description("키"),
                            fieldWithPath("result[].memberProfile.bodyType").description("체형"),
                            fieldWithPath("result[].memberProfile.job").description("직업"),
                            fieldWithPath("result[].memberProfile.images").description("프로필 이미지 목록"),
                            fieldWithPath("result[].memberProfile.images[].id").description("이미지 ID"),
                            fieldWithPath("result[].memberProfile.images[].fileName").description("이미지 파일명"),
                            fieldWithPath("result[].memberProfile.images[].fileUrl").description("이미지 URL"),
                            fieldWithPath("result[].memberProfile.images[].imageIndex").description("이미지 순서"),
                            fieldWithPath("result[].compatibilityScore").description("호환성 점수"),
                            fieldWithPath("result[].matchingType").description("매칭 타입"),
                            fieldWithPath("result[].createdAt").description("매칭 생성 시간"),
                            fieldWithPath("result[].expiresAt").description("매칭 만료 시간"),
                            fieldWithPath("result[].liked").description("좋아요 여부"),
                            fieldWithPath("result[].matched").description("매칭 성사 여부")
                    )
                ));
    }

    @Test
    @DisplayName("API - 활성화된 매칭 목록 조회")
    void getActiveMatchings() throws Exception {
        List<MatchingResultResponse> matchingResponses = createMatchingResponses("ANY_TYPE_FOR_ACTIVE"); 

        MatchingSuccess successCode = MatchingSuccess.GET_ACTIVE_MATCHINGS_SUCCESS;
        SuccessResponse<List<MatchingResultResponse>> apiResponseBody =
                SuccessResponse.of(successCode, matchingResponses);

        doReturn(apiResponseBody).when(matchingController).getActiveMatchings(any(LoginMember.class));

        this.mockMvc.perform(get("/api/matching/active")
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("matching-active-get",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("result").description("매칭 결과 목록"),
                                fieldWithPath("result[].matchingResultId").description("매칭 결과 ID"),
                                fieldWithPath("result[].memberProfile").description("매칭된 회원 프로필 정보"),
                                fieldWithPath("result[].memberProfile.id").description("회원 ID"),
                                fieldWithPath("result[].memberProfile.nickname").description("닉네임"),
                                fieldWithPath("result[].memberProfile.gender").description("성별"),
                                fieldWithPath("result[].memberProfile.age").description("나이"),
                                fieldWithPath("result[].memberProfile.location").description("지역"),
                                fieldWithPath("result[].memberProfile.height").description("키"),
                                fieldWithPath("result[].memberProfile.bodyType").description("체형"),
                                fieldWithPath("result[].memberProfile.job").description("직업"),
                                fieldWithPath("result[].memberProfile.images").description("프로필 이미지 목록"),
                                fieldWithPath("result[].memberProfile.images[].id").description("이미지 ID"),
                                fieldWithPath("result[].memberProfile.images[].fileName").description("이미지 파일명"),
                                fieldWithPath("result[].memberProfile.images[].fileUrl").description("이미지 URL"),
                                fieldWithPath("result[].memberProfile.images[].imageIndex").description("이미지 순서"),
                                fieldWithPath("result[].compatibilityScore").description("호환성 점수"),
                                fieldWithPath("result[].matchingType").description("매칭 타입"),
                                fieldWithPath("result[].createdAt").description("매칭 생성 시간"),
                                fieldWithPath("result[].expiresAt").description("매칭 만료 시간"),
                                fieldWithPath("result[].liked").description("좋아요 여부"),
                                fieldWithPath("result[].matched").description("매칭 성사 여부")
                        )
                ));
    }

    @Test
    @DisplayName("API - 매칭 성사된 결과 목록 조회")
    void getMatchedResults() throws Exception {
        List<MatchedResponse> matchingResponses = createMatchedResponses("ANY_TYPE_FOR_MATCHED"); 

        MatchingSuccess successCode = MatchingSuccess.GET_MATCHED_RESULTS_SUCCESS;
        SuccessResponse<List<MatchedResponse>> apiResponseBody =
                SuccessResponse.of(successCode, matchingResponses);

        doReturn(apiResponseBody).when(matchingController).getMatchedResults(any(LoginMember.class));

        this.mockMvc.perform(get("/api/matching/matched")
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("matching-matched-get",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("result").description("매칭 결과 목록"),
                                fieldWithPath("result[].matchedId").description("매칭 성사 ID"),
                                fieldWithPath("result[].memberProfile").description("매칭된 회원 프로필 정보"),
                                fieldWithPath("result[].memberProfile.id").description("회원 ID"),
                                fieldWithPath("result[].memberProfile.nickname").description("닉네임"),
                                fieldWithPath("result[].memberProfile.gender").description("성별"),
                                fieldWithPath("result[].memberProfile.age").description("나이"),
                                fieldWithPath("result[].memberProfile.location").description("지역"),
                                fieldWithPath("result[].memberProfile.height").description("키"),
                                fieldWithPath("result[].memberProfile.bodyType").description("체형"),
                                fieldWithPath("result[].memberProfile.job").description("직업"),
                                fieldWithPath("result[].memberProfile.images").description("프로필 이미지 목록"),
                                fieldWithPath("result[].memberProfile.images[].id").description("이미지 ID"),
                                fieldWithPath("result[].memberProfile.images[].fileName").description("이미지 파일명"),
                                fieldWithPath("result[].memberProfile.images[].fileUrl").description("이미지 URL"),
                                fieldWithPath("result[].memberProfile.images[].imageIndex").description("이미지 순서"),
                                fieldWithPath("result[].compatibilityScore").description("호환성 점수"),
                                fieldWithPath("result[].matchingType").description("매칭 타입"),
                                fieldWithPath("result[].createdAt").description("매칭 생성 시간")
                        )
                ));
    }

    @Test
    @DisplayName("API - 좋아요 보내기")
    void likeMatching() throws Exception {
        MatchingResultResponse matchingResponse = createMatchingResponses("ANY_TYPE_FOR_LIKE").get(0);
        Long matchingResultId = matchingResponse.matchingResultId();

        MatchingSuccess successCode = MatchingSuccess.LIKE_MATCHING_SUCCESS;
        SuccessResponse<MatchingResultResponse> apiResponseBody =
                SuccessResponse.of(successCode, matchingResponse);

        doReturn(apiResponseBody).when(matchingController).likeMatching(any(LoginMember.class), any(Long.class));

        this.mockMvc.perform(post("/api/matching/{matchingResultId}/like", matchingResultId)
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("matching-like",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("matchingResultId").description("매칭 결과 ID")
                        ),
                        responseFields(
                            fieldWithPath("code").description("응답 코드"),
                            fieldWithPath("result").description("매칭 결과"),
                            fieldWithPath("result.matchingResultId").description("매칭 결과 ID"),
                            fieldWithPath("result.memberProfile").description("매칭된 회원 프로필 정보"),
                            fieldWithPath("result.memberProfile.id").description("회원 ID"),
                            fieldWithPath("result.memberProfile.nickname").description("닉네임"),
                            fieldWithPath("result.memberProfile.gender").description("성별"),
                            fieldWithPath("result.memberProfile.age").description("나이"),
                            fieldWithPath("result.memberProfile.location").description("지역"),
                            fieldWithPath("result.memberProfile.height").description("키"),
                            fieldWithPath("result.memberProfile.bodyType").description("체형"),
                            fieldWithPath("result.memberProfile.job").description("직업"),
                            fieldWithPath("result.memberProfile.images").description("프로필 이미지 목록"),
                            fieldWithPath("result.memberProfile.images[].id").description("이미지 ID"),
                            fieldWithPath("result.memberProfile.images[].fileName").description("이미지 파일명/URL"),
                            fieldWithPath("result.memberProfile.images[].fileUrl").description("이미지 타입"),
                            fieldWithPath("result.memberProfile.images[].imageIndex").description("이미지 순서"),
                            fieldWithPath("result.compatibilityScore").description("호환성 점수"),
                            fieldWithPath("result.matchingType").description("매칭 타입"),
                            fieldWithPath("result.createdAt").description("매칭 생성 시간"),
                            fieldWithPath("result.expiresAt").description("매칭 만료 시간"),
                            fieldWithPath("result.liked").description("좋아요 여부"),
                            fieldWithPath("result.matched").description("매칭 성사 여부")
                    )
                ));
    }
    
    private List<MatchingResultResponse> createMatchingResponses(String matchingType) {
        List<MatchingResultResponse> responses = new ArrayList<>();
        
        List<ImageListResponse> images1 = new ArrayList<>();
        images1.add(new ImageListResponse(1L, "image1.jpg", "https://right-bucket.s3.ap-northeast-2.amazonaws.com/profile-images/image1.jpg", 1));
        images1.add(new ImageListResponse(2L, "image2.jpg", "https://right-bucket.s3.ap-northeast-2.amazonaws.com/profile-images/image2.jpg", 2));
        
        MatchingResultResponse.MemberProfileDto profile1 = new MatchingResultResponse.MemberProfileDto(
                2L, "사용자1", "여성", 28, "서울", 165, "보통", "개발자", images1
        );
        
        responses.add(new MatchingResultResponse(
                1L, profile1, matchingType.equals("FREE") ? 80 : (matchingType.equals("PREMIUM") ? 90 : 85),
                matchingType, LocalDateTime.now(), LocalDateTime.now().plusDays(7),
                false, false
        ));
        
        List<ImageListResponse> images2 = new ArrayList<>();
        images2.add(new ImageListResponse(3L, "image3.jpg", "https://right-bucket.s3.ap-northeast-2.amazonaws.com/profile-images/image3.jpg", 1));
        
        MatchingResultResponse.MemberProfileDto profile2 = new MatchingResultResponse.MemberProfileDto(
                3L, "사용자2", "여성", 25, "부산", 170, "슬림", "디자이너", images2
        );
        
        responses.add(new MatchingResultResponse(
                2L, profile2, matchingType.equals("FREE") ? 75 : (matchingType.equals("PREMIUM") ? 85 : 80),
                matchingType, LocalDateTime.now(), LocalDateTime.now().plusDays(7),
                false, false
        ));
        
        return responses;
    }
    
    private List<MatchedResponse> createMatchedResponses(String matchingType) {
        List<MatchedResponse> responses = new ArrayList<>();
        
        List<ImageListResponse> images = new ArrayList<>();
        images.add(new ImageListResponse(1L, "image1.jpg", "https://right-bucket.s3.ap-northeast-2.amazonaws.com/profile-images/image1.jpg", 1));
        images.add(new ImageListResponse(2L, "image2.jpg", "https://right-bucket.s3.ap-northeast-2.amazonaws.com/profile-images/image2.jpg", 2));
        
        MatchedResponse.MemberProfileDto profile1 = new MatchedResponse.MemberProfileDto(
                2L, "매칭1", "여성", 28, "서울", 165, "보통", "개발자", images
        );
        
        responses.add(new MatchedResponse(
                1L, profile1, matchingType.equals("FREE") ? 80 : (matchingType.equals("PREMIUM") ? 90 : 85), 
                matchingType, LocalDateTime.now()
        ));
        
        return responses;
    }
}