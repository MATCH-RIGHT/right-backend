package com.example.rightbackend.rekognition.service;

import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.rekognition.domain.FaceFeature;
import com.example.rightbackend.rekognition.domain.MyFaceFeature;
import com.example.rightbackend.rekognition.domain.repository.FaceFeatureRepository;
import com.example.rightbackend.rekognition.domain.repository.MyFaceFeatureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FaceFeatureBitmaskService {

    private final FaceFeatureRepository faceFeatureRepository;
    private final MyFaceFeatureRepository myFaceFeatureRepository;
    private final FaceFeatureBitmaskUtil bitmaskUtil;

    public FaceFeatureBitmaskService(
            FaceFeatureRepository faceFeatureRepository,
            MyFaceFeatureRepository myFaceFeatureRepository,
            FaceFeatureBitmaskUtil bitmaskUtil) {
        this.faceFeatureRepository = faceFeatureRepository;
        this.myFaceFeatureRepository = myFaceFeatureRepository;
        this.bitmaskUtil = bitmaskUtil;
    }

    @Transactional
    public void updateMemberFaceFeatureBitmask(MemberProfile memberProfile) {
        List<MyFaceFeature> myFaceFeatures = memberProfile.getMyFaceFeatures();
        
        List<Long> featureIds = myFaceFeatures.stream()
                .map(myFaceFeature -> myFaceFeature.getFaceFeature().getId())
                .collect(Collectors.toList());
        
        BigInteger bitmask = bitmaskUtil.convertToBitmask(featureIds);
        memberProfile.setFaceFeaturesBitmask(bitmask);
    }

    @Transactional
    public void syncFaceFeatureFromBitmask(MemberProfile memberProfile) {
        List<MyFaceFeature> existingFeatures = memberProfile.getMyFaceFeatures();
        memberProfile.getMyFaceFeatures().clear();
        myFaceFeatureRepository.deleteAll(existingFeatures);
        
        BigInteger bitmask = memberProfile.getFaceFeaturesBitmask();
        List<FaceFeature> features = bitmaskUtil.getFeaturesFromBitmask(bitmask);
        
        for (FaceFeature feature : features) {
            MyFaceFeature myFaceFeature = MyFaceFeature.of(memberProfile, feature);
            memberProfile.getMyFaceFeatures().add(myFaceFeature);
            myFaceFeatureRepository.save(myFaceFeature);
        }
    }

    @Transactional
    public void addFaceFeature(MemberProfile memberProfile, Long featureId) {
        FaceFeature feature = faceFeatureRepository.findById(featureId)
                .orElseThrow(() -> new RestApiException(MemberError.INVALID_FEATURE_INDEX));
        
        int index = bitmaskUtil.getBitmaskIndex(featureId);
        memberProfile.addFaceFeatureToBitmask(feature, index);
        
        MyFaceFeature myFaceFeature = MyFaceFeature.of(memberProfile, feature);
        memberProfile.getMyFaceFeatures().add(myFaceFeature);
        myFaceFeatureRepository.save(myFaceFeature);
    }

    @Transactional
    public void removeFaceFeature(MemberProfile memberProfile, Long featureId) {
        int index = bitmaskUtil.getBitmaskIndex(featureId);
        memberProfile.removeFaceFeatureFromBitmask(index);
        
        List<MyFaceFeature> toRemove = memberProfile.getMyFaceFeatures().stream()
                .filter(myFaceFeature -> myFaceFeature.getFaceFeature().getId().equals(featureId))
                .collect(Collectors.toList());
        
        memberProfile.getMyFaceFeatures().removeAll(toRemove);
        myFaceFeatureRepository.deleteAll(toRemove);
    }

    public boolean hasFaceFeature(MemberProfile memberProfile, Long featureId) {
        int index = bitmaskUtil.getBitmaskIndex(featureId);
        return memberProfile.hasFaceFeature(index);
    }

    public double calculateFaceFeatureSimilarity(MemberProfile profile1, MemberProfile profile2) {
        BigInteger bitmask1 = profile1.getFaceFeaturesBitmask();
        BigInteger bitmask2 = profile2.getFaceFeaturesBitmask();
        
        BigInteger andResult = bitmask1.and(bitmask2);
        int commonFeatureCount = andResult.bitCount();
        
        BigInteger orResult = bitmask1.or(bitmask2);
        int totalFeatureCount = orResult.bitCount();
        
        if (totalFeatureCount == 0) {
            return 0.0;
        }
        
        return (double) commonFeatureCount / totalFeatureCount;
    }
}