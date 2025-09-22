package com.example.rightbackend.image;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.MemberRole;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.member.controller.dto.EncodeMember;
import com.example.rightbackend.image.controller.dto.S3File;
import com.example.rightbackend.image.controller.dto.response.ImageListResponse;
import com.example.rightbackend.image.domain.MemberImage;
import com.example.rightbackend.image.domain.repository.MemberImageRepository;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.ImageError;
import com.example.rightbackend.image.service.ImageService;
import com.example.rightbackend.image.service.S3Uploader;
import com.example.rightbackend.rekognition.controller.dto.response.FaceFeatureListResponse;
import com.example.rightbackend.rekognition.domain.FaceFeature;
import com.example.rightbackend.rekognition.domain.repository.FaceFeatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;
    
    @Mock
    private MemberRepository memberRepository;
    
    @Mock
    private S3Uploader s3Uploader;
    
    @Mock
    private FaceFeatureRepository faceFeatureRepository;

    @Mock
    private MemberImageRepository memberImageRepository;

    private Member testMember;
    private LoginMember loginMember;
    
    @BeforeEach
    void setUp() {
        // Member 객체 생성
        EncodeMember encodeMember = new EncodeMember(
                "테스트 사용자",
                "test-provider-id",
                "test-password",
                "010-1234-5678"
        );
        testMember = Member.of(encodeMember);
        testMember.setId(1L);
        testMember.setMemberImage(new ArrayList<>());
        loginMember = new LoginMember(testMember.getId(), MemberRole.MEMBER);
    }
    
    @Test
    @DisplayName("이미지 목록 조회 테스트")
    void getImageListTest() {
        // Given
        List<MemberImage> memberImages = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MemberImage image = MemberImage.of(
                    "test-image-" + (i + 1) + ".jpg",
                    "http://example.com/test-image-" + (i + 1) + ".jpg"
            );
            image.setImageIndex(i + 1);
            image.setMember(testMember);
            memberImages.add(image);
        }
        testMember.setMemberImage(memberImages);
        
        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        
        // When
        List<ImageListResponse> result = imageService.getImageList(loginMember);
        
        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        
        boolean hasIndex1 = result.stream().anyMatch(img -> img.imageIndex() == 1);
        boolean hasIndex2 = result.stream().anyMatch(img -> img.imageIndex() == 2);
        boolean hasIndex3 = result.stream().anyMatch(img -> img.imageIndex() == 3);
        
        assertTrue(hasIndex1);
        assertTrue(hasIndex2);
        assertTrue(hasIndex3);
    }
    
    @Test
    @DisplayName("이미지 업로드 시 인덱스 할당 테스트")
    void uploadWithIndexTest() {
        // Given
        List<MemberImage> existingImages = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            MemberImage image = MemberImage.of(
                    "test-image-" + (i + 1) + ".jpg",
                    "http://example.com/test-image-" + (i + 1) + ".jpg"
            );
            image.setImageIndex(i + 1);
            image.setMember(testMember);
            existingImages.add(image);
        }
        testMember.setMemberImage(existingImages);
        
        MockMultipartFile newImage = new MockMultipartFile(
                "newImage", 
                "new-image.jpg", 
                "image/jpeg", 
                "test image data".getBytes()
        );
        List<MultipartFile> newImages = List.of(newImage);
        
        S3File s3File = new S3File("new-image.jpg", "http://example.com/new-image.jpg");
        when(s3Uploader.multiUpload(newImages)).thenReturn(List.of(s3File));
        
        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member savedMember = invocation.getArgument(0);
            return savedMember;
        });
        
        // When
        imageService.multiUpload(loginMember, newImages);
        
        // Then
        List<MemberImage> allImages = testMember.getMemberImage();
        
        assertNotNull(allImages);
        assertEquals(3, allImages.size());
        
        boolean hasIndex1 = allImages.stream()
                .anyMatch(img -> img.getImageIndex() == 1);
        boolean hasIndex2 = allImages.stream()
                .anyMatch(img -> img.getImageIndex() == 2);
        boolean hasIndex3 = allImages.stream()
                .anyMatch(img -> img.getImageIndex() == 3);
        
        assertTrue(hasIndex1);
        assertTrue(hasIndex2);
        assertTrue(hasIndex3);
    }
    
    @Test
    @DisplayName("얼굴 특징 목록 조회 테스트")
    void getAllFaceFeaturesTest() {
        // Given
        List<FaceFeature> faceFeatures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            FaceFeature feature = FaceFeature.of("특징" + (i + 1));
            feature.setId((long) (i + 1));
            faceFeatures.add(feature);
        }
        
        when(faceFeatureRepository.findAllByOrderByIdAsc()).thenReturn(faceFeatures);
        
        // When
        FaceFeatureListResponse response = imageService.getAllFaceFeatures();
        
        // Then
        assertNotNull(response);
        assertEquals(5, response.getFaceFeatures().size());
        
        boolean hasFeature1 = response.getFaceFeatures().stream()
                .anyMatch(f -> f.getId() == 1L && "특징1".equals(f.getName()));
        boolean hasFeature5 = response.getFaceFeatures().stream()
                .anyMatch(f -> f.getId() == 5L && "특징5".equals(f.getName()));
        
        assertTrue(hasFeature1);
        assertTrue(hasFeature5);
    }

    @Test
    @DisplayName("이미지 삭제 - imageId 사용")
    void deleteImageByIdTest() {
        // Given
        MemberImage imageToDelete = MemberImage.of(
                "test-image-1.jpg",
                "http://example.com/test-image-1.jpg"
        );
        imageToDelete.setId(1L);
        imageToDelete.setImageIndex(1);
        imageToDelete.setMember(testMember);

        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(memberImageRepository.findById(1L)).thenReturn(Optional.of(imageToDelete));
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When & Then
        assertDoesNotThrow(() -> imageService.deleteImage(loginMember, 1L));
    }

    @Test
    @DisplayName("이미지 삭제 - 권한 없는 이미지 접근 시 예외 발생")
    void deleteImageUnauthorizedTest() {
        // Given
        EncodeMember otherEncodeMember = new EncodeMember(
                "다른 사용자",
                "other-provider-id",
                "other-password",
                "010-9999-9999"
        );
        Member otherMember = Member.of(otherEncodeMember);
        otherMember.setId(2L);

        MemberImage imageToDelete = MemberImage.of(
                "test-image-1.jpg",
                "http://example.com/test-image-1.jpg"
        );
        imageToDelete.setId(1L);
        imageToDelete.setMember(otherMember);

        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(memberImageRepository.findById(1L)).thenReturn(Optional.of(imageToDelete));

        // When & Then
        RestApiException exception = assertThrows(RestApiException.class,
                () -> imageService.deleteImage(loginMember, 1L));

        assertEquals(ImageError.UNAUTHORIZED_IMAGE_ACCESS.getMessage(),
                exception.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("이미지 순서 변경 테스트")
    void reorderImagesTest() {
        // Given
        List<MemberImage> memberImages = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            MemberImage image = MemberImage.of(
                    "test-image-" + i + ".jpg",
                    "http://example.com/test-image-" + i + ".jpg"
            );
            image.setId((long) i);
            image.setImageIndex(i);
            image.setMember(testMember);
            memberImages.add(image);
        }
        testMember.setMemberImage(memberImages);

        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 순서를 3, 1, 2로 변경
        List<Long> newOrder = List.of(3L, 1L, 2L);

        // When
        List<ImageListResponse> result = imageService.reorderImages(loginMember, newOrder);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());

        // 첫 번째 이미지가 id=3인지 확인
        assertEquals(3L, result.get(0).id());
        assertEquals(1, result.get(0).imageIndex());

        // 두 번째 이미지가 id=1인지 확인
        assertEquals(1L, result.get(1).id());
        assertEquals(2, result.get(1).imageIndex());

        // 세 번째 이미지가 id=2인지 확인
        assertEquals(2L, result.get(2).id());
        assertEquals(3, result.get(2).imageIndex());
    }

    @Test
    @DisplayName("이미지 순서 변경 - 잘못된 이미지 ID 리스트")
    void reorderImagesInvalidTest() {
        // Given
        List<MemberImage> memberImages = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            MemberImage image = MemberImage.of(
                    "test-image-" + i + ".jpg",
                    "http://example.com/test-image-" + i + ".jpg"
            );
            image.setId((long) i);
            image.setImageIndex(i);
            image.setMember(testMember);
            memberImages.add(image);
        }
        testMember.setMemberImage(memberImages);

        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));

        // 잘못된 ID 포함 (ID 4는 존재하지 않음)
        List<Long> invalidOrder = List.of(1L, 2L, 4L);

        // When & Then
        RestApiException exception = assertThrows(RestApiException.class,
                () -> imageService.reorderImages(loginMember, invalidOrder));

        assertEquals(ImageError.INVALID_IMAGE_ORDER.getMessage(),
                exception.getErrorCode().getMessage());
    }
}