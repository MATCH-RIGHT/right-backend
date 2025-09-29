package com.example.rightbackend.rekognition.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.MemberRole;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.domain.repository.MemberProfileRepository;
import com.example.rightbackend.rekognition.domain.FaceFeature;
import com.example.rightbackend.rekognition.domain.Feature.FeatureMapper;
import com.example.rightbackend.rekognition.domain.repository.FaceFeatureRepository;
import com.example.rightbackend.rekognition.domain.repository.MyFaceFeatureRepository;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RekognitionServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FaceFeatureRepository faceFeatureRepository;

    @Mock
    private FeatureMapper featureMapper;

    @Mock
    private RekognitionClient rekognitionClient;

    @Mock
    private S3FaceAnalysis s3FaceAnalysis;

    @Mock
    private MemberProfileRepository memberProfileRepository;

    @Mock
    private MyFaceFeatureRepository myFaceFeatureRepository;

    @InjectMocks
    private RekognitionService rekognitionService;

    @Test
    @DisplayName("회원의 얼굴 특징을 ID 포함하여 조회할 수 있다")
    void getFaceFeatureTest() {
        // given
        LoginMember loginMember = new LoginMember(1L, MemberRole.ADMIN);
        Member member = mock(Member.class);
        MemberProfile memberProfile = mock(MemberProfile.class);
        List<String> featureNames = Arrays.asList("긴 얼굴형", "고양이상");

        FaceFeature faceFeature1 = FaceFeature.of("긴 얼굴형");
        faceFeature1.setId(1L);
        FaceFeature faceFeature2 = FaceFeature.of("고양이상");
        faceFeature2.setId(2L);

        // when
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(member.getMemberProfile()).thenReturn(memberProfile);
        when(memberProfile.getFaceAnalysisResponse()).thenReturn(featureNames);
        when(faceFeatureRepository.findByName("긴 얼굴형")).thenReturn(Optional.of(faceFeature1));
        when(faceFeatureRepository.findByName("고양이상")).thenReturn(Optional.of(faceFeature2));
        when(featureMapper.getFeatureTypeByName("긴 얼굴형")).thenReturn("FACE_SHAPE");
        when(featureMapper.getFeatureTypeByName("고양이상")).thenReturn("ANIMAL_LOOK");
        when(featureMapper.getIdByName("긴 얼굴형")).thenReturn(1);
        when(featureMapper.getIdByName("고양이상")).thenReturn(1);

        List<Map<String, Object>> result = rekognitionService.getFaceFeature(loginMember);

        // then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).get("id"));
        assertEquals("긴 얼굴형", result.get(0).get("name"));
        assertEquals("FACE_SHAPE", result.get(0).get("featureType"));
        assertEquals(1, result.get(0).get("featureValueId"));
    }

    @Test
    @DisplayName("회원의 얼굴 특징을 ID 형태로 조회할 수 있다")
    void getFaceFeatureIdsTest() {
        // given
        LoginMember loginMember = new LoginMember(1L, MemberRole.ADMIN);
        Member member = mock(Member.class);
        MemberProfile memberProfile = mock(MemberProfile.class);
        List<String> featureNames = Arrays.asList("긴 얼굴형", "고양이상", "동안");

        Map<String, List<Integer>> expectedGrouped = new HashMap<>();
        expectedGrouped.put("FACE_SHAPE", Arrays.asList(1));
        expectedGrouped.put("ANIMAL_LOOK", Arrays.asList(1));
        expectedGrouped.put("AGE", Arrays.asList(1));

        // when
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(member.getMemberProfile()).thenReturn(memberProfile);
        when(memberProfile.getFaceAnalysisResponse()).thenReturn(featureNames);
        when(featureMapper.groupIdsByFeatureType(featureNames)).thenReturn(expectedGrouped);

        Map<String, List<Integer>> result = rekognitionService.getFaceFeatureIds(loginMember);

        // then
        assertEquals(3, result.size());
        assertTrue(result.containsKey("FACE_SHAPE"));
        assertTrue(result.containsKey("ANIMAL_LOOK"));
        assertTrue(result.containsKey("AGE"));
    }

    @Test
    @DisplayName("ID를 사용하여 얼굴 특징을 저장할 수 있다")
    void saveFaceFeaturesByIdsTest() {
        // given
        LoginMember loginMember = new LoginMember(1L, MemberRole.ADMIN);
        Member member = mock(Member.class);
        MemberProfile memberProfile = mock(MemberProfile.class);

        Map<String, List<Integer>> featureIds = new HashMap<>();
        featureIds.put("FACE_SHAPE", Arrays.asList(1, 2));
        featureIds.put("ANIMAL_LOOK", Arrays.asList(1));

        FaceFeature feature1 = FaceFeature.of("긴 얼굴형");
        FaceFeature feature2 = FaceFeature.of("계란형");
        FaceFeature feature3 = FaceFeature.of("고양이상");

        // when
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(member.getMemberProfile()).thenReturn(memberProfile);
        when(featureMapper.getNameById("FACE_SHAPE", 1)).thenReturn("긴 얼굴형");
        when(featureMapper.getNameById("FACE_SHAPE", 2)).thenReturn("계란형");
        when(featureMapper.getNameById("ANIMAL_LOOK", 1)).thenReturn("고양이상");
        when(faceFeatureRepository.findByName("긴 얼굴형")).thenReturn(Optional.of(feature1));
        when(faceFeatureRepository.findByName("계란형")).thenReturn(Optional.of(feature2));
        when(faceFeatureRepository.findByName("고양이상")).thenReturn(Optional.of(feature3));

        rekognitionService.saveFaceFeaturesByIds(loginMember, featureIds);

        // then
        verify(memberProfile, times(4)).getMyFaceFeatures(); // 1 for clear() + 3 for adds
    }

    @Test
    @DisplayName("존재하지 않는 Feature는 새로 생성하여 저장한다")
    void saveFaceFeaturesByIds_CreateNewFeature() {
        // given
        LoginMember loginMember = new LoginMember(1L, MemberRole.ADMIN);
        Member member = mock(Member.class);
        MemberProfile memberProfile = mock(MemberProfile.class);

        Map<String, List<Integer>> featureIds = new HashMap<>();
        featureIds.put("FACE_SHAPE", Arrays.asList(1));

        FaceFeature newFeature = FaceFeature.of("긴 얼굴형");

        // when
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(member.getMemberProfile()).thenReturn(memberProfile);
        when(featureMapper.getNameById("FACE_SHAPE", 1)).thenReturn("긴 얼굴형");
        when(faceFeatureRepository.findByName("긴 얼굴형")).thenReturn(Optional.empty());
        when(faceFeatureRepository.save(any(FaceFeature.class))).thenReturn(newFeature);

        rekognitionService.saveFaceFeaturesByIds(loginMember, featureIds);

        // then
        verify(memberProfile, times(2)).getMyFaceFeatures(); // 1 for clear() + 1 for add
        verify(faceFeatureRepository).save(argThat(feature ->
            "긴 얼굴형".equals(feature.getName())
        ));
    }
}