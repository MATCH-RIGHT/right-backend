package com.example.rightbackend.image;

import com.example.rightbackend.global.BaseIntegrationTest;
import com.example.rightbackend.image.controller.dto.S3File;
import com.example.rightbackend.image.service.S3Uploader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class S3UploaderTest extends BaseIntegrationTest {

    @Autowired S3Uploader s3Uploader;

    @Test
    @DisplayName("S3 이미지 업로드 테스트")
    void uploadTest() {
        // GIVEN
        String path = "test.png";
        String contentType = "image/png";
        MockMultipartFile image = new MockMultipartFile("test", path, contentType, "test".getBytes());
        List<MultipartFile> request = List.of(image, image, image, image);

        // WHEN
        List<S3File> S3Files = s3Uploader.multiUpload(request);

        // Then
        Assertions.assertThat(S3Files).isNotEmpty();
    }

    @Test
    @DisplayName("S3 이미지 삭제 테스트")
    void deleteTest() {
        // Given
        String fileName = "test.png";

        // When
        boolean result = s3Uploader.delete(fileName);

        // Then
        org.junit.jupiter.api.Assertions.assertTrue(result);
    }
}