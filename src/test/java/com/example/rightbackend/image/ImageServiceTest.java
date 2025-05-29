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
    
    private Member testMember;
    private LoginMember loginMember;
    
    @BeforeEach
    void setUp() {
        // Member 객체 생성
        EncodeMember encodeMember = new EncodeMember(
                "테스트 사용자",
                "test-provider",
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
}