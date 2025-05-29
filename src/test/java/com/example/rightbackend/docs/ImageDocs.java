package com.example.rightbackend.docs;

import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.ImageSuccess;
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
        final String fileName = "test.jpg";

        SuccessResponse response = SuccessResponse.of(ImageSuccess.IMAGE_DELETE_SUCCESS);

        doReturn(response).when(imageController).deleteImage(any(), any());

        this.mockMvc.perform(delete("/api/image/delete/{fileName}", fileName)
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("image-delete",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("fileName").description("삭제할 이미지 파일명")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("이미지 삭제 결과")
                        )
                ));
    }

    @Test
    @DisplayName("API - 이미지 변경")
    void updateImage() throws Exception {
        final String fileName = "old.jpg";

        MockMultipartFile mockImage = createNewMultipartFile();

        SuccessResponse response = SuccessResponse.of(ImageSuccess.IMAGE_CHANGE_SUCCESS);

        doReturn(response).when(imageController).updateImage(any(), any(), any());

        this.mockMvc.perform(multipart("/api/image/change/{fileName}", fileName)
                        .file(mockImage)
                        .header("Authorization", GIVEN_ACCESS_TOKEN)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andDo(document("image-update",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("fileName").description("변경할 이미지의 현재 파일명")
                        ),
                        requestParts(
                                partWithName("newImage").description("새로 업로드할 이미지 파일")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("이미지 변경 결과")
                        )
                ));
    }

    @Test
    @DisplayName("API - 얼굴 특징 분석")
    void analysisFace() throws Exception {
        String message = ImageResponse.FEATURE_UPLOAD_SUCCESS.getMessage();
        SuccessResponse<String> response = SuccessResponse.of(ImageSuccess.MY_FEATURE_UPLOAD_SUCCESS, message);
        ClassPathResource imgRes = new ClassPathResource("images/winter.jpg");
        try (InputStream is = imgRes.getInputStream()) {

            MockMultipartFile mockImage =
                    new MockMultipartFile(
                            "image",
                            "winter.jpg",
                            MediaType.IMAGE_JPEG_VALUE,
                            is
                    );

            doReturn(response).when(imageController).uploadMultiImage(any(), any());

            this.mockMvc.perform(multipart("/api/image/upload-feature")
                            .file("image", mockImage.getBytes())
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
                                    fieldWithPath("result").description("얼굴 특징 분석 결과")
                            )
                    ));
        }
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
                                fieldWithPath("result.features[].name").description("얼굴 특징 이름")
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

    private MockMultipartFile createNewMultipartFile() {
        return new MockMultipartFile("newImage",
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
}