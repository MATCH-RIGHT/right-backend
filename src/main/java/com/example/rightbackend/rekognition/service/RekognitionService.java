package com.example.rightbackend.rekognition.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.ImageError;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.domain.repository.MemberProfileRepository;
import com.example.rightbackend.rekognition.controller.dto.detection.FaceDetection;
import com.example.rightbackend.rekognition.domain.FaceFeature;
import com.example.rightbackend.rekognition.domain.Feature.FeatureMapper;
import com.example.rightbackend.rekognition.domain.MyFaceFeature;
import com.example.rightbackend.rekognition.domain.repository.FaceFeatureRepository;
import com.example.rightbackend.rekognition.domain.repository.MyFaceFeatureRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RekognitionService {

    private final RekognitionClient rekognitionClient;
    private final S3FaceAnalysis s3FaceAnalysis;
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final FaceFeatureRepository faceFeatureRepository;
    private final MyFaceFeatureRepository myFaceFeatureRepository;
    private final FeatureMapper featureMapper;

    public RekognitionService(RekognitionClient rekognitionClient, S3FaceAnalysis s3FaceAnalysis,
                            MemberRepository memberRepository, MemberProfileRepository memberProfileRepository,
                            FaceFeatureRepository faceFeatureRepository, MyFaceFeatureRepository myFaceFeatureRepository,
                            FeatureMapper featureMapper) {
        this.rekognitionClient = rekognitionClient;
        this.s3FaceAnalysis = s3FaceAnalysis;
        this.memberRepository = memberRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.faceFeatureRepository = faceFeatureRepository;
        this.myFaceFeatureRepository = myFaceFeatureRepository;
        this.featureMapper = featureMapper;
    }

    @Transactional
    public void detectFaces(final LoginMember loginMember, final MultipartFile imageFile) {
        try {
            SdkBytes imageBytes = SdkBytes.fromInputStream(imageFile.getInputStream());

            Image image = Image.builder()
                    .bytes(imageBytes)
                    .build();

            DetectFacesRequest request = DetectFacesRequest.builder()
                    .image(image)
                    .attributes(Attribute.ALL)
                    .build();

            DetectFacesResponse awsResponse = rekognitionClient.detectFaces(request);

            Member member = getMember(loginMember);
            MemberProfile memberProfile = member.getMemberProfile();

            List<String> faceAnalysisResponse = s3FaceAnalysis.analyzeFaceFeatures(FaceDetection.from(awsResponse).faceDetails(), memberProfile);

            addFaceFeature(faceAnalysisResponse, memberProfile);

//            return faceAnalysisResponse;

        } catch (RekognitionException | IOException e) {
            throw new RestApiException(ImageError.IMAGE_NOT_DETECTION);
        }
    }

    public void addFaceFeature(List<String> features, MemberProfile memberProfile) {
        for (String feature : features) {
            if (feature != null) {
                addFeatureAndCreateRelation(feature, memberProfile);
            }
        }
    }

    private void addFeatureAndCreateRelation(String featureName, MemberProfile memberProfile) {
        FaceFeature faceFeature = faceFeatureRepository.findByName(featureName)
                .orElseGet(() -> {
                    FaceFeature newFeature = FaceFeature.of(featureName);
                    return faceFeatureRepository.save(newFeature);
                });

        MyFaceFeature myFaceFeature = MyFaceFeature.of(memberProfile, faceFeature);
        myFaceFeatureRepository.save(myFaceFeature);
    }

    private Member getMember(LoginMember loginMember) {
        return memberRepository.findById(loginMember.memberId()).orElseThrow(() -> new RestApiException(MemberError.NULL_MEMBER));
    }

    public List<Map<String, Object>> getFaceFeature(final LoginMember loginMember) {
        Member member = getMember(loginMember);
        MemberProfile memberProfile = member.getMemberProfile();
        List<String> featureNames = memberProfile.getFaceAnalysisResponse();

        List<Map<String, Object>> featuresWithId = new ArrayList<>();

        for (String featureName : featureNames) {
            FaceFeature faceFeature = faceFeatureRepository.findByName(featureName).orElse(null);
            if (faceFeature != null) {
                Map<String, Object> featureMap = new HashMap<>();
                featureMap.put("id", faceFeature.getId());
                featureMap.put("name", featureName);
                featureMap.put("featureType", featureMapper.getFeatureTypeByName(featureName));
                featureMap.put("featureValueId", featureMapper.getIdByName(featureName));
                featuresWithId.add(featureMap);
            }
        }

        return featuresWithId;
    }

    public Map<String, List<Integer>> getFaceFeatureIds(final LoginMember loginMember) {
        Member member = getMember(loginMember);
        MemberProfile memberProfile = member.getMemberProfile();
        List<String> featureNames = memberProfile.getFaceAnalysisResponse();

        return featureMapper.groupIdsByFeatureType(featureNames);
    }

    @Transactional
    public void saveFaceFeaturesByIds(final LoginMember loginMember, Map<String, List<Integer>> featureIds) {
        Member member = getMember(loginMember);
        MemberProfile memberProfile = member.getMemberProfile();

        // Clear existing face features
        memberProfile.getMyFaceFeatures().clear();

        for (Map.Entry<String, List<Integer>> entry : featureIds.entrySet()) {
            String featureType = entry.getKey();
            List<Integer> ids = entry.getValue();

            for (Integer id : ids) {
                String name = featureMapper.getNameById(featureType, id);
                if (name != null) {
                    FaceFeature feature = faceFeatureRepository.findByName(name)
                            .orElseGet(() -> faceFeatureRepository.save(FaceFeature.of(name)));

                    MyFaceFeature myFaceFeature = MyFaceFeature.of(memberProfile, feature);
                    memberProfile.getMyFaceFeatures().add(myFaceFeature);
                }
            }
        }
    }
}