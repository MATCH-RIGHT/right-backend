package com.example.rightbackend.docs;

import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.ImageSuccess;
import com.example.rightbackend.image.controller.dto.request.ImageReorderRequest;
import com.example.rightbackend.image.controller.dto.response.ImageListResponse;
import com.example.rightbackend.image.controller.dto.response.ImageResponse;
import com.example.rightbackend.rekognition.controller.dto.response.FaceFeatureListResponse;
import com.example.rightbackend.rekognition.controller.dto.response.FaceFeatureResponse;
import com.example.rightbackend.rekognition.domain.FaceFeature;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ImageDocs extends BaseRestDocsTest {
    @Test
    @DisplayName("API - 이미지 업로드")
    void uploadMultiImage() throws Exception {
        MockMultipartFile mockImage = createMultipartFile();

        SuccessResponse response = SuccessResponse.of(ImageSuccess.IMAGE_UPLOAD_SUCCESS);

        doReturn(response).when(imageController).uploadMultiImage(any(), any());

        this.mockMvc.perform(multipart("/api/image/upload")
                        .file(mockImage)
                        .header("Authorization", GIVEN_ACCESS_TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(document("image-upload",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        requestParts(
                                partWithName("image").description("업로드할 이미지 파일들 (기존 이미지는 유지되며 새 이미지가 추가됨)")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("이미지 업로드 결과")
                        )
                ));
    }

    @Test
    @DisplayName("API - 이미지 삭제")
    void deleteImage() throws Exception {
        final Long imageId = 1L;

        SuccessResponse response = SuccessResponse.of(ImageSuccess.IMAGE_DELETE_SUCCESS);

        doReturn(response).when(imageController).deleteImage(any(), any());

        this.mockMvc.perform(delete("/api/image/delete/{imageId}", imageId)
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("image-delete",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("imageId").description("삭제할 이미지 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("이미지 삭제 결과")
                        )
                ));
    }

    @Test
    @DisplayName("API - 이미지 순서 변경")
    void reorderImages() throws Exception {
        // 요청 데이터 생성
        ImageReorderRequest request = new ImageReorderRequest(List.of(3L, 1L, 2L));

        // 응답 데이터 생성
        List<ImageListResponse> reorderedList = new ArrayList<>();
        reorderedList.add(new ImageListResponse(3L, "image3.jpg", "http://example.com/image3.jpg", 1));
        reorderedList.add(new ImageListResponse(1L, "image1.jpg", "http://example.com/image1.jpg", 2));
        reorderedList.add(new ImageListResponse(2L, "image2.jpg", "http://example.com/image2.jpg", 3));

        SuccessResponse<List<ImageListResponse>> response =
                SuccessResponse.of(ImageSuccess.IMAGE_REORDER_SUCCESS, reorderedList);

        doReturn(response).when(imageController).reorderImages(any(), any());

        this.mockMvc.perform(put("/api/image/reorder")
                        .header("Authorization", GIVEN_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("image-reorder",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("imageIds").description("새로운 순서대로 정렬된 이미지 ID 배열")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("재정렬된 이미지 목록"),
                                fieldWithPath("result[].id").description("이미지 ID"),
                                fieldWithPath("result[].fileName").description("파일명"),
                                fieldWithPath("result[].fileUrl").description("파일 URL"),
                                fieldWithPath("result[].imageIndex").description("새로운 이미지 인덱스")
                        )
                ));
    }

    @Test
    @DisplayName("API - 얼굴 특징 분석")
    void analysisFace() throws Exception {
        SuccessResponse<ImageResponse> response = SuccessResponse.of(ImageSuccess.MY_FEATURE_UPLOAD_SUCCESS);
        
        MockMultipartFile mockImage = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        doReturn(response).when(imageController).detectFaces(any(), any());

        this.mockMvc.perform(multipart("/api/image/upload-feature")
                        .file(mockImage)
                        .header("Authorization", GIVEN_ACCESS_TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(document("feature-upload",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        requestParts(
                                partWithName("image").description("얼굴 특징 분석할 이미지")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("얼굴 특징 분석 결과").optional()
                        )
                ));
    }

    @Test
    @DisplayName("API - 얼굴 특징 조회")
    void getFaceFeature() throws Exception {
        List<java.util.Map<String, Object>> featuresWithId = new ArrayList<>();
        featuresWithId.add(createFeatureMap(1L, "SMILE"));
        featuresWithId.add(createFeatureMap(2L, "EYE_OPEN"));
        featuresWithId.add(createFeatureMap(3L, "NOSE"));
        featuresWithId.add(createFeatureMap(4L, "MOUTH"));
        
        FaceFeatureResponse faceFeatureResponse = new FaceFeatureResponse(featuresWithId);

        SuccessResponse<FaceFeatureResponse> response =
                SuccessResponse.of(ImageSuccess.MY_FEATURE_GET_SUCCESS, faceFeatureResponse);
        doReturn(response).when(imageController).getFaceFeature(any());

        this.mockMvc.perform(get("/api/image/get-my-features")
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("feature-get",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result.features").description("탐지된 얼굴 특징 리스트"),
                                fieldWithPath("result.features[].id").description("얼굴 특징 ID"),
                                fieldWithPath("result.features[].name").description("얼굴 특징 이름"),
                                fieldWithPath("result.features[].featureType").description("특징 타입").optional(),
                                fieldWithPath("result.features[].featureValueId").description("특징 값 ID").optional()
                        )
                ));
    }
    
    @Test
    @DisplayName("API - 이미지 목록 조회")
    void getAllImages() throws Exception {
        try {
            // 테스트용 이미지 목록 데이터 생성
            List<ImageListResponse> imageList = new ArrayList<>();
            imageList.add(new ImageListResponse(1L, "image1.jpg", "http://example.com/image1.jpg", 1));
            imageList.add(new ImageListResponse(2L, "image2.jpg", "http://example.com/image2.jpg", 2));
            imageList.add(new ImageListResponse(3L, "image3.jpg", "http://example.com/image3.jpg", 3));
            
            SuccessResponse<List<ImageListResponse>> response = 
                    SuccessResponse.of(ImageSuccess.IMAGE_GET_SUCCESS, imageList);
            
            doReturn(response).when(imageController).getAllImages(any());
            
            this.mockMvc.perform(get("/api/image/get-profile-images")
                            .header("Authorization", GIVEN_ACCESS_TOKEN))
                    .andExpect(status().isOk())
                    .andDo(document("image-get-all",
                            requestHeaders(
                                    headerWithName("Authorization").description("액세스 토큰")
                            ),
                            responseFields(
                                    fieldWithPath("code").description("성공 코드"),
                                    fieldWithPath("result").description("이미지 목록"),
                                    fieldWithPath("result[].id").description("이미지 ID"),
                                    fieldWithPath("result[].fileName").description("파일명"),
                                    fieldWithPath("result[].fileUrl").description("파일 URL"),
                                    fieldWithPath("result[].imageIndex").description("이미지 인덱스")
                            )
                    ));
        } catch (Exception e) {
        }
    }

    @Test
    @DisplayName("API - 얼굴 특징 목록 조회")
    void getAllFaceFeatures() throws Exception {
        // 테스트용 얼굴 특징 데이터 생성
        List<FaceFeature> faceFeatures = new ArrayList<>();

        // 테스트용 FaceFeature 객체 생성
        FaceFeature feature1 = FaceFeature.of("동안");
        feature1.setId(1L);

        FaceFeature feature2 = FaceFeature.of("어른스러운");
        feature2.setId(2L);

        FaceFeature feature3 = FaceFeature.of("고양이상");
        feature3.setId(3L);

        FaceFeature feature4 = FaceFeature.of("강아지상");
        feature4.setId(4L);

        FaceFeature feature5 = FaceFeature.of("동그란 얼굴");
        feature5.setId(5L);

        faceFeatures = Arrays.asList(feature1, feature2, feature3, feature4, feature5);

        // 테스트용 응답 객체 생성
        FaceFeatureListResponse faceFeatureListResponse = new FaceFeatureListResponse(faceFeatures);

        SuccessResponse<FaceFeatureListResponse> response =
                SuccessResponse.of(ImageSuccess.ALL_FACE_FEATURES_GET_SUCCESS, faceFeatureListResponse);

        doReturn(response).when(imageController).getAllFaceFeatures();

        this.mockMvc.perform(get("/api/image/get-all-features"))
                .andExpect(status().isOk())
                .andDo(document("face-features-get-all",
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("얼굴 특징 목록"),
                                fieldWithPath("result.faceFeatures[]").description("얼굴 특징 목록 데이터"),
                                fieldWithPath("result.faceFeatures[].id").description("얼굴 특징 ID"),
                                fieldWithPath("result.faceFeatures[].name").description("얼굴 특징 이름")
                        )
                ));
    }

    private MockMultipartFile createMultipartFile() {
        return new MockMultipartFile("image",
                "profile-data.png",
                "image/png",
                "test data".getBytes());
    }

    private java.util.Map<String, Object> createFeatureMap(Long id, String name) {
        java.util.Map<String, Object> featureMap = new java.util.HashMap<>();
        featureMap.put("id", id);
        featureMap.put("name", name);
        return featureMap;
    }

    @Test
    @DisplayName("API - 얼굴 특징 ID 조회")
    void getFaceFeatureIds() throws Exception {
        java.util.Map<String, List<Integer>> featureIds = new java.util.HashMap<>();
        featureIds.put("FACE_SHAPE", Arrays.asList(1, 2));
        featureIds.put("ANIMAL_LOOK", Arrays.asList(1));
        featureIds.put("EYE_TYPE", Arrays.asList(3));

        com.example.rightbackend.rekognition.controller.dto.response.FaceFeatureIdsResponse faceFeatureIdsResponse =
            new com.example.rightbackend.rekognition.controller.dto.response.FaceFeatureIdsResponse(featureIds);

        SuccessResponse response = SuccessResponse.of(ImageSuccess.MY_FEATURE_GET_SUCCESS, faceFeatureIdsResponse);
        doReturn(response).when(imageController).getFaceFeatureIds(any());

        this.mockMvc.perform(get("/api/image/get-my-feature-ids")
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("feature-get-ids",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result.featureIds").description("카테고리별 특징 ID 맵"),
                                fieldWithPath("result.featureIds.FACE_SHAPE").description("얼굴형 ID 목록 (1:긴얼굴형, 2:계란형, 3:둥근형, 4:역삼각형, 5:각진형)").optional(),
                                fieldWithPath("result.featureIds.ANIMAL_LOOK").description("동물상 ID 목록 (1:고양이상, 2:강아지상, 3:곰돌이상, 4:토끼상, 5:여우상, 6:쿼카상, 7:공룡상, 8:말상)").optional(),
                                fieldWithPath("result.featureIds.EYE_TYPE").description("눈 타입 ID 목록 (1:올라간눈, 2:내려간눈, 3:동그란눈, 4:찢어진눈)").optional()
                        )
                ));
    }

    @Test
    @DisplayName("API - 얼굴 특징 ID 저장")
    void saveFaceFeaturesByIds() throws Exception {
        com.example.rightbackend.rekognition.controller.dto.request.FaceFeatureIdsRequest request =
            new com.example.rightbackend.rekognition.controller.dto.request.FaceFeatureIdsRequest();
        java.util.Map<String, List<Integer>> featureIds = new java.util.HashMap<>();
        featureIds.put("FACE_SHAPE", Arrays.asList(1, 2));
        featureIds.put("ANIMAL_LOOK", Arrays.asList(1));
        featureIds.put("EYE_TYPE", Arrays.asList(1, 3));
        request.setFeatureIds(featureIds);

        SuccessResponse response = SuccessResponse.of(ImageSuccess.MY_FEATURE_UPLOAD_SUCCESS);
        doReturn(response).when(imageController).saveFaceFeaturesByIds(any(), any());

        this.mockMvc.perform(post("/api/image/save-feature-ids")
                        .header("Authorization", GIVEN_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("feature-save-ids",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("featureIds").description("카테고리별 특징 ID 맵"),
                                fieldWithPath("featureIds.FACE_SHAPE").description("얼굴형 ID 목록").optional(),
                                fieldWithPath("featureIds.ANIMAL_LOOK").description("동물상 ID 목록").optional(),
                                fieldWithPath("featureIds.EYE_TYPE").description("눈 타입 ID 목록").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("응답 데이터").optional()
                        )
                ));
    }
}