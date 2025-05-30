package com.example.rightbackend.rekognition.service;

import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.rekognition.domain.FaceFeature;
import com.example.rightbackend.rekognition.domain.repository.FaceFeatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FaceFeatureBitmaskUtilTest {

    @Mock
    private FaceFeatureRepository faceFeatureRepository;

    @InjectMocks
    private FaceFeatureBitmaskUtil faceFeatureBitmaskUtil;

    private List<FaceFeature> testFeatures;

    @BeforeEach
    void setUp() {
        testFeatures = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            FaceFeature feature = FaceFeature.of("테스트 특징 " + i);
            setId(feature, (long) i);
            testFeatures.add(feature);
        }
        
        // Mock 설정
        when(faceFeatureRepository.findAllByOrderByIdAsc()).thenReturn(testFeatures);
    }
    
    private void setId(FaceFeature feature, Long id) {
        try {
            java.lang.reflect.Field idField = FaceFeature.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(feature, id);
        } catch (Exception e) {
            throw new RuntimeException("ID 설정 중 오류 발생", e);
        }
    }

    @Test
    @DisplayName("얼굴 특징 ID로 비트마스크 인덱스를 정확히 조회할 수 있다")
    void getBitmaskIndexTest() {
        // given
        faceFeatureBitmaskUtil.initializeBitmaskIndices();
        Long featureId = testFeatures.get(0).getId();
        
        // when
        int index = faceFeatureBitmaskUtil.getBitmaskIndex(featureId);
        
        // then
        assertThat(index).isEqualTo(0);
    }

    @Test
    @DisplayName("비트마스크 인덱스로 얼굴 특징 ID를 정확히 조회할 수 있다")
    void getFeatureIdTest() {
        // given
        faceFeatureBitmaskUtil.initializeBitmaskIndices();
        Long expectedFeatureId = testFeatures.get(0).getId();
        
        // when
        Long actualFeatureId = faceFeatureBitmaskUtil.getFeatureId(0);
        
        // then
        assertThat(actualFeatureId).isEqualTo(expectedFeatureId);
    }

    @Test
    @DisplayName("얼굴 특징 ID 목록을 비트마스크로 변환할 수 있다")
    void convertToBitmaskTest() {
        // given
        faceFeatureBitmaskUtil.initializeBitmaskIndices();
        List<Long> featureIds = new ArrayList<>();
        for (FaceFeature feature : testFeatures) {
            featureIds.add(feature.getId());
        }
        
        // when
        BigInteger bitmask = faceFeatureBitmaskUtil.convertToBitmask(featureIds);
        
        // then
        for (int i = 0; i < testFeatures.size(); i++) {
            assertThat(bitmask.testBit(i)).isTrue();
        }
    }

    @Test
    @DisplayName("비트마스크를 얼굴 특징 ID 목록으로 변환할 수 있다")
    void convertToFeatureIdsTest() {
        // given
        faceFeatureBitmaskUtil.initializeBitmaskIndices();
        List<Long> originalFeatureIds = new ArrayList<>();
        for (FaceFeature feature : testFeatures) {
            originalFeatureIds.add(feature.getId());
        }
        
        BigInteger bitmask = BigInteger.ZERO;
        for (int i = 0; i < testFeatures.size(); i++) {
            bitmask = bitmask.setBit(i);
        }
        
        // when
        List<Long> convertedFeatureIds = faceFeatureBitmaskUtil.convertToFeatureIds(bitmask);
        
        // then
        assertThat(convertedFeatureIds).containsExactlyInAnyOrderElementsOf(originalFeatureIds);
    }

    @Test
    @DisplayName("존재하지 않는 얼굴 특징 ID로 조회 시 예외가 발생한다")
    void getBitmaskIndexWithInvalidIdTest() {
        // given
        faceFeatureBitmaskUtil.initializeBitmaskIndices();
        Long invalidFeatureId = 999L;
        
        // when & then
        assertThrows(RestApiException.class, () -> {
            faceFeatureBitmaskUtil.getBitmaskIndex(invalidFeatureId);
        });
    }

    @Test
    @DisplayName("전체 얼굴 특징 수를 정확히 조회할 수 있다")
    void getTotalFeatureCountTest() {
        // given
        faceFeatureBitmaskUtil.initializeBitmaskIndices();
        
        // when
        int totalCount = faceFeatureBitmaskUtil.getTotalFeatureCount();
        
        // then
        assertThat(totalCount).isEqualTo(testFeatures.size());
    }
}