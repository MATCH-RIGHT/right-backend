package com.example.rightbackend.matching.filter.service;

import com.example.rightbackend.rekognition.domain.Feature.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdealFaceFeatureUtil {

    private static final Map<String, Integer> featureIndexMap = new HashMap<>();
    private static final List<FeatureInfo> featureInfoList = new ArrayList<>();

    static {
        // 얼굴형
        int index = 0;
        for (FaceShape value : FaceShape.values()) {
            featureIndexMap.put(value.name(), index);
            featureInfoList.add(new FeatureInfo("얼굴형", value.name(), value.getName(), index));
            index++;
        }

        // 눈 타입
        for (EyeType value : EyeType.values()) {
            featureIndexMap.put(value.name(), index);
            featureInfoList.add(new FeatureInfo("눈 타입", value.name(), value.getName(), index));
            index++;
        }

        // 눈 크기
        for (EyeSize value : EyeSize.values()) {
            featureIndexMap.put(value.name(), index);
            featureInfoList.add(new FeatureInfo("눈 크기", value.name(), value.getName(), index));
            index++;
        }

        // 입술 모양
        for (LipShape value : LipShape.values()) {
            featureIndexMap.put(value.name(), index);
            featureInfoList.add(new FeatureInfo("입술 모양", value.name(), value.getName(), index));
            index++;
        }

        // 코 모양
        for (NoseShape value : NoseShape.values()) {
            featureIndexMap.put(value.name(), index);
            featureInfoList.add(new FeatureInfo("코 모양", value.name(), value.getName(), index));
            index++;
        }

        // 턱 모양
        for (JawShape value : JawShape.values()) {
            featureIndexMap.put(value.name(), index);
            featureInfoList.add(new FeatureInfo("턱 모양", value.name(), value.getName(), index));
            index++;
        }

        // 이마
        for (ForeHead value : ForeHead.values()) {
            featureIndexMap.put(value.name(), index);
            featureInfoList.add(new FeatureInfo("이마", value.name(), value.getName(), index));
            index++;
        }

        // 피부 톤
        for (SkinTone value : SkinTone.values()) {
            featureIndexMap.put(value.name(), index);
            featureInfoList.add(new FeatureInfo("피부 톤", value.name(), value.getName(), index));
            index++;
        }

        // 수염
        for (Beard value : Beard.values()) {
            featureIndexMap.put(value.name(), index);
            featureInfoList.add(new FeatureInfo("수염", value.name(), value.getName(), index));
            index++;
        }

        // 안경
        for (Glass value : Glass.values()) {
            featureIndexMap.put(value.name(), index);
            featureInfoList.add(new FeatureInfo("안경", value.name(), value.getName(), index));
            index++;
        }

        // 동물상
        for (AnimalLook value : AnimalLook.values()) {
            featureIndexMap.put(value.name(), index);
            featureInfoList.add(new FeatureInfo("동물상", value.name(), value.getName(), index));
            index++;
        }

        // 나이
        for (Age value : Age.values()) {
            featureIndexMap.put(value.name(), index);
            featureInfoList.add(new FeatureInfo("나이", value.name(), value.getName(), index));
            index++;
        }
    }

    public static Integer getFeatureIndex(String featureName) {
        return featureIndexMap.get(featureName);
    }

    public static List<FeatureInfo> getAllFeatureInfos() {
        return new ArrayList<>(featureInfoList);
    }

    public static List<FeatureInfo> getFeatureInfosFromBitmask(BigInteger bitmask) {
        List<FeatureInfo> selectedFeatures = new ArrayList<>();
        
        for (FeatureInfo featureInfo : featureInfoList) {
            if (bitmask.testBit(featureInfo.index())) {
                selectedFeatures.add(featureInfo);
            }
        }
        
        return selectedFeatures;
    }
    
    public record FeatureInfo(
            String category,
            String code,
            String name,
            int index
    ) {}
}