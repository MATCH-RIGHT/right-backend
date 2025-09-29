package com.example.rightbackend.docs;

import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.MatchingFilterSuccess;
import com.example.rightbackend.matching.filter.controller.dto.request.MatchingFilterRequest;
import com.example.rightbackend.matching.filter.controller.dto.request.MatchingFilterIdsRequest;
import com.example.rightbackend.matching.filter.controller.dto.response.MatchingFilterResponse;
import com.example.rightbackend.matching.filter.controller.dto.response.MatchingFilterIdsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Test
    @DisplayName("API - 매칭 필터 조회")
    void getMatchingFilterWithIds() throws Exception {
        Map<String, List<Integer>> featureIds = new HashMap<>();
        featureIds.put("FACE_SHAPE", Arrays.asList(1, 2));
        featureIds.put("ANIMAL_LOOK", Arrays.asList(1));
        featureIds.put("EYE_TYPE", Arrays.asList(3));

        List<MatchingFilterIdsResponse.FeatureWithId> features = List.of(
            new MatchingFilterIdsResponse.FeatureWithId("FACE_SHAPE", "LONG", "긴 얼굴형", 0, 1),
            new MatchingFilterIdsResponse.FeatureWithId("FACE_SHAPE", "OVAL", "계란형", 1, 2),
            new MatchingFilterIdsResponse.FeatureWithId("ANIMAL_LOOK", "CAT", "고양이상", 10, 1),
            new MatchingFilterIdsResponse.FeatureWithId("EYE_TYPE", "ROUND", "동그란눈", 20, 3)
        );

        MatchingFilterIdsResponse.RegionDto region = new MatchingFilterIdsResponse.RegionDto(1L, "서울");
        MatchingFilterIdsResponse responseData = new MatchingFilterIdsResponse(
            1L, 20, 30, BigInteger.valueOf(3), featureIds, features, region
        );

        SuccessResponse<MatchingFilterIdsResponse> response =
            SuccessResponse.of(MatchingFilterSuccess.GET_MATCHING_FILTER_SUCCESS, responseData);

        doReturn(response).when(matchingFilterController).getMatchingFilterWithIds(any());

        this.mockMvc.perform(get("/api/matching/filter/v2")
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("matching-filter-get-v2",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result.id").description("매칭 필터 ID"),
                                fieldWithPath("result.minAge").description("최소 나이"),
                                fieldWithPath("result.maxAge").description("최대 나이"),
                                fieldWithPath("result.idealFaceFeaturesBitmask").description("이상형 얼굴 특징 비트마스크"),
                                fieldWithPath("result.idealFaceFeatureIds").description("카테고리별 특징 ID 맵"),
                                fieldWithPath("result.idealFaceFeatureIds.FACE_SHAPE").description("얼굴형 ID 목록 (1:긴얼굴형, 2:계란형, 3:둥근형, 4:역삼각형, 5:각진형)").optional(),
                                fieldWithPath("result.idealFaceFeatureIds.ANIMAL_LOOK").description("동물상 ID 목록 (1:고양이상, 2:강아지상, 3:곰돌이상, 4:토끼상, 5:여우상, 6:쿼카상, 7:공룡상, 8:말상)").optional(),
                                fieldWithPath("result.idealFaceFeatureIds.EYE_TYPE").description("눈 타입 ID 목록 (1:올라간눈, 2:내려간눈, 3:동그란눈, 4:찢어진눈)").optional(),
                                fieldWithPath("result.idealFaceFeatures").description("이상형 얼굴 특징 상세 정보 리스트"),
                                fieldWithPath("result.idealFaceFeatures[].category").description("특징 카테고리"),
                                fieldWithPath("result.idealFaceFeatures[].code").description("특징 코드"),
                                fieldWithPath("result.idealFaceFeatures[].name").description("특징 이름"),
                                fieldWithPath("result.idealFaceFeatures[].index").description("특징 인덱스"),
                                fieldWithPath("result.idealFaceFeatures[].featureId").description("특징 ID"),
                                fieldWithPath("result.region").description("지역 정보").optional(),
                                fieldWithPath("result.region.id").description("지역 ID").optional(),
                                fieldWithPath("result.region.name").description("지역 이름").optional()
                        )
                ));
    }

    @Test
    @DisplayName("API - 매칭 필터 생성/수정")
    void createOrUpdateMatchingFilterWithIds() throws Exception {
        Map<String, List<Integer>> requestFeatureIds = new HashMap<>();
        requestFeatureIds.put("FACE_SHAPE", Arrays.asList(1, 2));
        requestFeatureIds.put("ANIMAL_LOOK", Arrays.asList(1));

        MatchingFilterIdsRequest request = new MatchingFilterIdsRequest(20, 30, requestFeatureIds, 1L);

        Map<String, List<Integer>> responseFeatureIds = new HashMap<>();
        responseFeatureIds.put("FACE_SHAPE", Arrays.asList(1, 2));
        responseFeatureIds.put("ANIMAL_LOOK", Arrays.asList(1));

        List<MatchingFilterIdsResponse.FeatureWithId> features = List.of(
            new MatchingFilterIdsResponse.FeatureWithId("FACE_SHAPE", "LONG", "긴 얼굴형", 0, 1),
            new MatchingFilterIdsResponse.FeatureWithId("FACE_SHAPE", "OVAL", "계란형", 1, 2),
            new MatchingFilterIdsResponse.FeatureWithId("ANIMAL_LOOK", "CAT", "고양이상", 10, 1)
        );

        MatchingFilterIdsResponse.RegionDto region = new MatchingFilterIdsResponse.RegionDto(1L, "서울");
        MatchingFilterIdsResponse responseData = new MatchingFilterIdsResponse(
            1L, 20, 30, BigInteger.valueOf(3), responseFeatureIds, features, region
        );

        SuccessResponse<MatchingFilterIdsResponse> response =
            SuccessResponse.of(MatchingFilterSuccess.UPDATE_MATCHING_FILTER_SUCCESS, responseData);

        doReturn(response).when(matchingFilterController).createOrUpdateMatchingFilterWithIds(any(), any());

        this.mockMvc.perform(post("/api/matching/filter/v2")
                        .header("Authorization", GIVEN_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("matching-filter-create-update-v2",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("minAge").description("최소 나이").optional(),
                                fieldWithPath("maxAge").description("최대 나이").optional(),
                                fieldWithPath("idealFaceFeatureIds").description("카테고리별 특징 ID 맵"),
                                fieldWithPath("idealFaceFeatureIds.FACE_SHAPE").description("얼굴형 ID 목록").optional(),
                                fieldWithPath("idealFaceFeatureIds.ANIMAL_LOOK").description("동물상 ID 목록").optional(),
                                fieldWithPath("regionId").description("지역 ID").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result.id").description("매칭 필터 ID"),
                                fieldWithPath("result.minAge").description("최소 나이"),
                                fieldWithPath("result.maxAge").description("최대 나이"),
                                fieldWithPath("result.idealFaceFeaturesBitmask").description("이상형 얼굴 특징 비트마스크"),
                                fieldWithPath("result.idealFaceFeatureIds").description("카테고리별 특징 ID 맵"),
                                fieldWithPath("result.idealFaceFeatureIds.FACE_SHAPE").description("얼굴형 ID 목록").optional(),
                                fieldWithPath("result.idealFaceFeatureIds.ANIMAL_LOOK").description("동물상 ID 목록").optional(),
                                fieldWithPath("result.idealFaceFeatures").description("이상형 얼굴 특징 상세 정보 리스트"),
                                fieldWithPath("result.idealFaceFeatures[].category").description("특징 카테고리"),
                                fieldWithPath("result.idealFaceFeatures[].code").description("특징 코드"),
                                fieldWithPath("result.idealFaceFeatures[].name").description("특징 이름"),
                                fieldWithPath("result.idealFaceFeatures[].index").description("특징 인덱스"),
                                fieldWithPath("result.idealFaceFeatures[].featureId").description("특징 ID"),
                                fieldWithPath("result.region").description("지역 정보").optional(),
                                fieldWithPath("result.region.id").description("지역 ID").optional(),
                                fieldWithPath("result.region.name").description("지역 이름").optional()
                        )
                ));
    }
}
