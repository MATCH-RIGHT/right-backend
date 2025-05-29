package com.example.rightbackend.rekognition.service;

import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.rekognition.domain.FaceFeature;
import com.example.rightbackend.rekognition.domain.MyFaceFeature;
import com.example.rightbackend.rekognition.domain.repository.FaceFeatureRepository;
import com.example.rightbackend.rekognition.domain.repository.MyFaceFeatureRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FaceFeatureBitmaskServiceTest {

    @Mock
    private FaceFeatureBitmaskUtil faceFeatureBitmaskUtil;

    @Mock
    private FaceFeatureRepository faceFeatureRepository;

    @Mock
    private MyFaceFeatureRepository myFaceFeatureRepository;
    
    @InjectMocks
    private FaceFeatureBitmaskService faceFeatureBitmaskService;

    @Mock
    private MemberProfile memberProfile;
    
    @Mock
    private FaceFeature faceFeature;
    
    @Mock
    private MyFaceFeature myFaceFeature;

    @Test
    @DisplayName("회원의 얼굴 특징을 비트마스크로 변환하여 저장할 수 있다")
    void updateMemberFaceFeatureBitmaskTest() {
        // given
        List<MyFaceFeature> myFaceFeatures = new ArrayList<>();
        myFaceFeatures.add(myFaceFeature);
        BigInteger expectedBitmask = BigInteger.valueOf(1);
        
        // when
        when(memberProfile.getMyFaceFeatures()).thenReturn(myFaceFeatures);
        when(myFaceFeature.getFaceFeature()).thenReturn(faceFeature);
        when(faceFeature.getId()).thenReturn(1L);
        when(faceFeatureBitmaskUtil.convertToBitmask(anyList())).thenReturn(expectedBitmask);
        
        faceFeatureBitmaskService.updateMemberFaceFeatureBitmask(memberProfile);
        
        // then
        verify(memberProfile).setFaceFeaturesBitmask(expectedBitmask);
    }

    @Test
    @DisplayName("비트마스크에서 MyFaceFeature 엔티티로 동기화할 수 있다")
    void syncFaceFeatureFromBitmaskTest() {
        // given
        BigInteger bitmask = BigInteger.valueOf(1);
        List<FaceFeature> features = Arrays.asList(faceFeature);
        List<MyFaceFeature> emptyList = new ArrayList<>();
        
        // when
        when(memberProfile.getFaceFeaturesBitmask()).thenReturn(bitmask);
        when(faceFeatureBitmaskUtil.getFeaturesFromBitmask(bitmask)).thenReturn(features);
        when(memberProfile.getMyFaceFeatures()).thenReturn(emptyList);
        
        faceFeatureBitmaskService.syncFaceFeatureFromBitmask(memberProfile);
        
        // then
        verify(myFaceFeatureRepository).save(any(MyFaceFeature.class));
    }

    @Test
    @DisplayName("회원에게 얼굴 특징을 추가할 수 있다")
    void addFaceFeatureTest() {
        // given
        Long featureId = 1L;
        int index = 0;
        
        // when
        when(faceFeatureRepository.findById(featureId)).thenReturn(Optional.of(faceFeature));
        when(faceFeatureBitmaskUtil.getBitmaskIndex(featureId)).thenReturn(index);
        
        faceFeatureBitmaskService.addFaceFeature(memberProfile, featureId);
        
        // then
        verify(memberProfile).addFaceFeatureToBitmask(faceFeature, index);
        verify(myFaceFeatureRepository).save(any(MyFaceFeature.class));
    }

    @Test
    @DisplayName("회원에게서 얼굴 특징을 제거할 수 있다")
    void removeFaceFeatureTest() {
        // given
        Long featureId = 1L;
        int index = 0;
        List<MyFaceFeature> myFaceFeatures = new ArrayList<>();
        myFaceFeatures.add(myFaceFeature);
        
        // when
        when(faceFeatureBitmaskUtil.getBitmaskIndex(featureId)).thenReturn(index);
        when(memberProfile.getMyFaceFeatures()).thenReturn(myFaceFeatures);
        when(myFaceFeature.getFaceFeature()).thenReturn(faceFeature);
        when(faceFeature.getId()).thenReturn(featureId);
        
        faceFeatureBitmaskService.removeFaceFeature(memberProfile, featureId);
        
        // then
        verify(memberProfile).removeFaceFeatureFromBitmask(index);
        verify(myFaceFeatureRepository).deleteAll(anyList());
    }

    @Test
    @DisplayName("회원이 특정 얼굴 특징을 가지고 있는지 확인할 수 있다")
    void hasFaceFeatureTest() {
        // given
        Long featureId = 1L;
        int index = 0;
        
        // when
        when(faceFeatureBitmaskUtil.getBitmaskIndex(featureId)).thenReturn(index);
        when(memberProfile.hasFaceFeature(index)).thenReturn(true);
        
        boolean hasFeature = faceFeatureBitmaskService.hasFaceFeature(memberProfile, featureId);
        
        // then
        assertTrue(hasFeature);
    }
    
    @Test
    @DisplayName("두 회원의 얼굴 특징 유사도를 계산할 수 있다")
    void calculateFaceFeatureSimilarityTest() {
        // given
        MemberProfile profile1 = mock(MemberProfile.class);
        MemberProfile profile2 = mock(MemberProfile.class);
        BigInteger bitmask1 = BigInteger.valueOf(6); // 110 (2와 1번 비트 설정)
        BigInteger bitmask2 = BigInteger.valueOf(3); // 011 (1과 0번 비트 설정)
        
        // when
        when(profile1.getFaceFeaturesBitmask()).thenReturn(bitmask1);
        when(profile2.getFaceFeaturesBitmask()).thenReturn(bitmask2);
        
        double similarity = faceFeatureBitmaskService.calculateFaceFeatureSimilarity(profile1, profile2);
        
        // then
        assertEquals(1.0 / 3.0, similarity, 0.001);
    }
}