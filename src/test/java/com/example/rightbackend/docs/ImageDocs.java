package com.example.rightbackend.docs;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.MemberSuccess;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

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
        // 테스트용 이미지 파일 생성
        MockMultipartFile mockImage = new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                "테스트 이미지 내용".getBytes()
        );

        // 응답 객체 생성
        SuccessResponse response = SuccessResponse.of(MemberSuccess.IMAGE_UPLOAD_SUCCESS);

        // 컨트롤러 응답 모킹
        doReturn(response).when(imageController).uploadMultiImage(any(), any());

        // API 요청 및 문서화
        this.mockMvc.perform(multipart("/image/upload")
                        .file(mockImage)
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("image-upload",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        requestParts(
                                partWithName("images").description("업로드할 이미지 파일들")
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
        // 테스트용 파일명
        final String fileName = "test.jpg";

        // 응답 객체 생성
        SuccessResponse response = SuccessResponse.of(MemberSuccess.IMAGE_DELETE_SUCCESS);

        // 컨트롤러 응답 모킹
        doReturn(response).when(imageController).deleteImage(any(), any());

        // API 요청 및 문서화
        this.mockMvc.perform(delete("/image/delete/{fileName}", fileName)
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

        MockMultipartFile newImage = new MockMultipartFile(
                "newImage",
                "new.jpg",
                "image/jpeg",
                "새 이미지 내용".getBytes()
        );

        SuccessResponse response = SuccessResponse.of(MemberSuccess.IMAGE_CHANGE_SUCCESS);

        doReturn(response).when(imageController).updateImage(any(), any(), any());

        this.mockMvc.perform(multipart("/image/change/{fileName}", fileName)
                        .file(newImage)
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

}