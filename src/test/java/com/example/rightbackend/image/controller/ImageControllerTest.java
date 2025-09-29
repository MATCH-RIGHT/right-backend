package com.example.rightbackend.image.controller;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.MemberRole;
import com.example.rightbackend.auth.service.TokenProvider;
import com.example.rightbackend.rekognition.controller.dto.request.FaceFeatureIdsRequest;
import com.example.rightbackend.rekognition.service.RekognitionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RekognitionService rekognitionService;

    @MockBean
    private TokenProvider tokenProvider;

    private LoginMember loginMember;

    @BeforeEach
    void setUp() {
        loginMember = new LoginMember(1L, MemberRole.MEMBER);
        when(tokenProvider.getLoginFromToken(anyString())).thenReturn(loginMember);
    }

    @Test
    @DisplayName("GET /api/image/get-my-features - 얼굴 특징을 ID와 함께 조회할 수 있다")
    void getFaceFeatureTest() throws Exception {
        // given
        List<Map<String, Object>> faceAnalysisResponse = new ArrayList<>();
        Map<String, Object> feature1 = new HashMap<>();
        feature1.put("id", 1L);
        feature1.put("name", "긴 얼굴형");
        feature1.put("featureType", "FACE_SHAPE");
        feature1.put("featureValueId", 1);
        faceAnalysisResponse.add(feature1);

        // when
        when(rekognitionService.getFaceFeature(any(LoginMember.class))).thenReturn(faceAnalysisResponse);

        // then
        mockMvc.perform(get("/api/image/get-my-features")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.features[0].id").value(1))
                .andExpect(jsonPath("$.result.features[0].name").value("긴 얼굴형"))
                .andExpect(jsonPath("$.result.features[0].featureType").value("FACE_SHAPE"))
                .andExpect(jsonPath("$.result.features[0].featureValueId").value(1));
    }

    @Test
    @DisplayName("GET /api/image/get-my-feature-ids - 얼굴 특징 ID들을 타입별로 그룹화하여 조회할 수 있다")
    void getFaceFeatureIdsTest() throws Exception {
        // given
        Map<String, List<Integer>> featureIds = new HashMap<>();
        featureIds.put("FACE_SHAPE", Arrays.asList(1, 2));
        featureIds.put("ANIMAL_LOOK", Arrays.asList(1));

        // when
        when(rekognitionService.getFaceFeatureIds(any(LoginMember.class))).thenReturn(featureIds);

        // then
        mockMvc.perform(get("/api/image/get-my-feature-ids")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.featureIds.FACE_SHAPE[0]").value(1))
                .andExpect(jsonPath("$.result.featureIds.FACE_SHAPE[1]").value(2))
                .andExpect(jsonPath("$.result.featureIds.ANIMAL_LOOK[0]").value(1));
    }

    @Test
    @DisplayName("POST /api/image/save-feature-ids - ID를 사용하여 얼굴 특징을 저장할 수 있다")
    void saveFaceFeaturesByIdsTest() throws Exception {
        // given
        FaceFeatureIdsRequest request = new FaceFeatureIdsRequest();
        Map<String, List<Integer>> featureIds = new HashMap<>();
        featureIds.put("FACE_SHAPE", Arrays.asList(1, 2));
        featureIds.put("ANIMAL_LOOK", Arrays.asList(1));
        request.setFeatureIds(featureIds);

        // when & then
        mockMvc.perform(post("/api/image/save-feature-ids")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(rekognitionService).saveFaceFeaturesByIds(any(LoginMember.class), eq(featureIds));
    }

    @Test
    @DisplayName("POST /api/image/save-feature-ids - 빈 ID 맵으로도 저장 요청이 가능하다")
    void saveFaceFeaturesByIds_EmptyMap() throws Exception {
        // given
        FaceFeatureIdsRequest request = new FaceFeatureIdsRequest();
        request.setFeatureIds(new HashMap<>());

        // when & then
        mockMvc.perform(post("/api/image/save-feature-ids")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(rekognitionService).saveFaceFeaturesByIds(any(LoginMember.class), any());
    }
}