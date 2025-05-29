package com.example.rightbackend.docs;

import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.MatchingFilterSuccess;
import com.example.rightbackend.matching.filter.controller.dto.request.MatchingFilterRequest;
import com.example.rightbackend.matching.filter.controller.dto.response.MatchingFilterResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigInteger;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MatchingFilterDocs extends BaseRestDocsTest {
    @Test
    @DisplayName("API - 매칭 필터 조회")
    void getMatchingFilter() throws Exception {
        MatchingFilterResponse.RegionDto region = new MatchingFilterResponse.RegionDto(1L, "서울");
        List<MatchingFilterResponse.FaceFeatureDto> features = List.of(
            new MatchingFilterResponse.FaceFeatureDto("category", "code", "name", 0)
        );
        MatchingFilterResponse responseData = new MatchingFilterResponse(
            1L, 20, 30, BigInteger.valueOf(3), features, region
        );
        SuccessResponse<MatchingFilterResponse> response = SuccessResponse.of(MatchingFilterSuccess.GET_MATCHING_FILTER_SUCCESS, responseData);

        doReturn(response).when(matchingFilterController).getMatchingFilter(any());

        this.mockMvc.perform(get("/api/matching/filter")
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("matching-filter-get",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result.id").description("매칭 필터 ID"),
                                fieldWithPath("result.minAge").description("최소 나이"),
                                fieldWithPath("result.maxAge").description("최대 나이"),
                                fieldWithPath("result.idealFaceFeaturesBitmask").description("이상형 얼굴 특징 비트마스크"),
                                fieldWithPath("result.idealFaceFeatures").description("이상형 얼굴 특징 리스트"),
                                fieldWithPath("result.idealFaceFeatures[].category").description("특징 카테고리"),
                                fieldWithPath("result.idealFaceFeatures[].code").description("특징 코드"),
                                fieldWithPath("result.idealFaceFeatures[].name").description("특징 이름"),
                                fieldWithPath("result.idealFaceFeatures[].index").description("특징 인덱스"),
                                fieldWithPath("result.region").description("지역 정보"),
                                fieldWithPath("result.region.id").description("지역 ID"),
                                fieldWithPath("result.region.name").description("지역 이름")
                        )
                ));
    }

    @Test
    @DisplayName("API - 매칭 필터 생성/수정")
    void createOrUpdateMatchingFilter() throws Exception {
        MatchingFilterRequest request = new MatchingFilterRequest(20, 30, List.of("동안", "강아지상"), 1L);
        MatchingFilterResponse.RegionDto region = new MatchingFilterResponse.RegionDto(1L, "서울");
        List<MatchingFilterResponse.FaceFeatureDto> features = List.of(
            new MatchingFilterResponse.FaceFeatureDto("category", "code", "name", 0)
        );
        MatchingFilterResponse responseData = new MatchingFilterResponse(
            1L, 20, 30, BigInteger.valueOf(3), features, region
        );
        SuccessResponse<MatchingFilterResponse> response = SuccessResponse.of(MatchingFilterSuccess.UPDATE_MATCHING_FILTER_SUCCESS, responseData);

        doReturn(response).when(matchingFilterController).createOrUpdateMatchingFilter(any(), any());

        this.mockMvc.perform(post("/api/matching/filter")
                        .header("Authorization", GIVEN_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("matching-filter-create-update",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("minAge").description("최소 나이"),
                                fieldWithPath("maxAge").description("최대 나이"),
                                fieldWithPath("idealFaceFeatures").description("이상형 얼굴 특징 리스트"),
                                fieldWithPath("regionId").description("지역 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result.id").description("매칭 필터 ID"),
                                fieldWithPath("result.minAge").description("최소 나이"),
                                fieldWithPath("result.maxAge").description("최대 나이"),
                                fieldWithPath("result.idealFaceFeaturesBitmask").description("이상형 얼굴 특징 비트마스크"),
                                fieldWithPath("result.idealFaceFeatures").description("이상형 얼굴 특징 리스트"),
                                fieldWithPath("result.idealFaceFeatures[].category").description("특징 카테고리"),
                                fieldWithPath("result.idealFaceFeatures[].code").description("특징 코드"),
                                fieldWithPath("result.idealFaceFeatures[].name").description("특징 이름"),
                                fieldWithPath("result.idealFaceFeatures[].index").description("특징 인덱스"),
                                fieldWithPath("result.region").description("지역 정보"),
                                fieldWithPath("result.region.id").description("지역 ID"),
                                fieldWithPath("result.region.name").description("지역 이름")
                        )
                ));
    }
}
