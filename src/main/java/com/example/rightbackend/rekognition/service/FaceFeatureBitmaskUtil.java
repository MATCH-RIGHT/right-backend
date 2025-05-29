package com.example.rightbackend.rekognition.service;

import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.rekognition.domain.FaceFeature;
import com.example.rightbackend.rekognition.domain.repository.FaceFeatureRepository;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FaceFeatureBitmaskUtil {

    private final FaceFeatureRepository faceFeatureRepository;
    private Map<Long, Integer> featureIdToIndexMap;
    private Map<Integer, Long> indexToFeatureIdMap;
    private boolean isInitialized = false;

    public FaceFeatureBitmaskUtil(FaceFeatureRepository faceFeatureRepository) {
        this.faceFeatureRepository = faceFeatureRepository;
        this.featureIdToIndexMap = new HashMap<>();
        this.indexToFeatureIdMap = new HashMap<>();
    }

    public void initializeBitmaskIndices() {
        if (isInitialized) {
            return;
        }

        List<FaceFeature> allFeatures = faceFeatureRepository.findAllByOrderByIdAsc();
        
        for (int i = 0; i < allFeatures.size(); i++) {
            FaceFeature feature = allFeatures.get(i);
            featureIdToIndexMap.put(feature.getId(), i);
            indexToFeatureIdMap.put(i, feature.getId());
        }
        
        isInitialized = true;
    }

    public int getBitmaskIndex(Long featureId) {
        if (!isInitialized) {
            initializeBitmaskIndices();
        }
        
        Integer index = featureIdToIndexMap.get(featureId);
        if (index == null) {
            throw new RestApiException(MemberError.INVALID_FEATURE_INDEX);
        }
        
        return index;
    }

    public Long getFeatureId(int index) {
        if (!isInitialized) {
            initializeBitmaskIndices();
        }
        
        Long featureId = indexToFeatureIdMap.get(index);
        if (featureId == null) {
            throw new RestApiException(MemberError.INVALID_FEATURE_INDEX);
        }
        
        return featureId;
    }

    public BigInteger convertToBitmask(List<Long> featureIds) {
        if (!isInitialized) {
            initializeBitmaskIndices();
        }
        
        BigInteger bitmask = BigInteger.ZERO;
        
        for (Long featureId : featureIds) {
            int index = getBitmaskIndex(featureId);
            bitmask = bitmask.setBit(index);
        }
        
        return bitmask;
    }

    public List<Long> convertToFeatureIds(BigInteger bitmask) {
        if (!isInitialized) {
            initializeBitmaskIndices();
        }
        
        List<Long> featureIds = indexToFeatureIdMap.entrySet().stream()
                .filter(entry -> bitmask.testBit(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        
        return featureIds;
    }

    public List<FaceFeature> getFeaturesFromBitmask(BigInteger bitmask) {
        List<Long> featureIds = convertToFeatureIds(bitmask);
        return faceFeatureRepository.findAllById(featureIds);
    }

    public int getTotalFeatureCount() {
        if (!isInitialized) {
            initializeBitmaskIndices();
        }
        
        return featureIdToIndexMap.size();
    }
}