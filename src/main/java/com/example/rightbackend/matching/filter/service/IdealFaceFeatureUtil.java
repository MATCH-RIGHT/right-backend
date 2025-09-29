package com.example.rightbackend.matching.filter.service;

import com.example.rightbackend.rekognition.domain.Feature.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IdealFaceFeatureUtil {

    private static final Map<String, Integer> featureIndexMap = new HashMap<>();
    private static final List<FeatureInfo> featureInfoList = new ArrayList<>();
    private static final Map<String, Map<Integer, FeatureInfo>> categoryIdMap = new HashMap<>();

    static {
        // 얼굴형
        int index = 0;
        Map<Integer, FeatureInfo> faceShapeMap = new HashMap<>();
        for (FaceShape value : FaceShape.values()) {
            featureIndexMap.put(value.name(), index);
            FeatureInfo info = new FeatureInfo("FACE_SHAPE", value.name(), value.getName(), index, value.getId());
            featureInfoList.add(info);
            faceShapeMap.put(value.getId(), info);
            index++;
        }
        categoryIdMap.put("FACE_SHAPE", faceShapeMap);

        // 눈 타입
        Map<Integer, FeatureInfo> eyeTypeMap = new HashMap<>();
        for (EyeType value : EyeType.values()) {
            featureIndexMap.put(value.name(), index);
            FeatureInfo info = new FeatureInfo("EYE_TYPE", value.name(), value.getName(), index, value.getId());
            featureInfoList.add(info);
            eyeTypeMap.put(value.getId(), info);
            index++;
        }
        categoryIdMap.put("EYE_TYPE", eyeTypeMap);

        // 눈 크기
        Map<Integer, FeatureInfo> eyeSizeMap = new HashMap<>();
        for (EyeSize value : EyeSize.values()) {
            featureIndexMap.put(value.name(), index);
            FeatureInfo info = new FeatureInfo("EYE_SIZE", value.name(), value.getName(), index, value.getId());
            featureInfoList.add(info);
            eyeSizeMap.put(value.getId(), info);
            index++;
        }
        categoryIdMap.put("EYE_SIZE", eyeSizeMap);

        // 입술 모양
        Map<Integer, FeatureInfo> lipShapeMap = new HashMap<>();
        for (LipShape value : LipShape.values()) {
            featureIndexMap.put(value.name(), index);
            FeatureInfo info = new FeatureInfo("LIP_SHAPE", value.name(), value.getName(), index, value.getId());
            featureInfoList.add(info);
            lipShapeMap.put(value.getId(), info);
            index++;
        }
        categoryIdMap.put("LIP_SHAPE", lipShapeMap);

        // 코 모양
        Map<Integer, FeatureInfo> noseShapeMap = new HashMap<>();
        for (NoseShape value : NoseShape.values()) {
            featureIndexMap.put(value.name(), index);
            FeatureInfo info = new FeatureInfo("NOSE_SHAPE", value.name(), value.getName(), index, value.getId());
            featureInfoList.add(info);
            noseShapeMap.put(value.getId(), info);
            index++;
        }
        categoryIdMap.put("NOSE_SHAPE", noseShapeMap);

        // 턱 모양
        Map<Integer, FeatureInfo> jawShapeMap = new HashMap<>();
        for (JawShape value : JawShape.values()) {
            featureIndexMap.put(value.name(), index);
            FeatureInfo info = new FeatureInfo("JAW_SHAPE", value.name(), value.getName(), index, value.getId());
            featureInfoList.add(info);
            jawShapeMap.put(value.getId(), info);
            index++;
        }
        categoryIdMap.put("JAW_SHAPE", jawShapeMap);

        // 이마
        Map<Integer, FeatureInfo> foreheadMap = new HashMap<>();
        for (ForeHead value : ForeHead.values()) {
            featureIndexMap.put(value.name(), index);
            FeatureInfo info = new FeatureInfo("FOREHEAD", value.name(), value.getName(), index, value.getId());
            featureInfoList.add(info);
            foreheadMap.put(value.getId(), info);
            index++;
        }
        categoryIdMap.put("FOREHEAD", foreheadMap);

        // 피부 톤
        Map<Integer, FeatureInfo> skinToneMap = new HashMap<>();
        for (SkinTone value : SkinTone.values()) {
            featureIndexMap.put(value.name(), index);
            FeatureInfo info = new FeatureInfo("SKIN_TONE", value.name(), value.getName(), index, value.getId());
            featureInfoList.add(info);
            skinToneMap.put(value.getId(), info);
            index++;
        }
        categoryIdMap.put("SKIN_TONE", skinToneMap);

        // 수염
        Map<Integer, FeatureInfo> beardMap = new HashMap<>();
        for (Beard value : Beard.values()) {
            featureIndexMap.put(value.name(), index);
            FeatureInfo info = new FeatureInfo("BEARD", value.name(), value.getName(), index, value.getId());
            featureInfoList.add(info);
            beardMap.put(value.getId(), info);
            index++;
        }
        categoryIdMap.put("BEARD", beardMap);

        // 안경
        Map<Integer, FeatureInfo> glassMap = new HashMap<>();
        for (Glass value : Glass.values()) {
            featureIndexMap.put(value.name(), index);
            FeatureInfo info = new FeatureInfo("GLASS", value.name(), value.getName(), index, value.getId());
            featureInfoList.add(info);
            glassMap.put(value.getId(), info);
            index++;
        }
        categoryIdMap.put("GLASS", glassMap);

        // 동물상
        Map<Integer, FeatureInfo> animalLookMap = new HashMap<>();
        for (AnimalLook value : AnimalLook.values()) {
            featureIndexMap.put(value.name(), index);
            FeatureInfo info = new FeatureInfo("ANIMAL_LOOK", value.name(), value.getName(), index, value.getId());
            featureInfoList.add(info);
            animalLookMap.put(value.getId(), info);
            index++;
        }
        categoryIdMap.put("ANIMAL_LOOK", animalLookMap);

        // 나이
        Map<Integer, FeatureInfo> ageMap = new HashMap<>();
        for (Age value : Age.values()) {
            featureIndexMap.put(value.name(), index);
            FeatureInfo info = new FeatureInfo("AGE", value.name(), value.getName(), index, value.getId());
            featureInfoList.add(info);
            ageMap.put(value.getId(), info);
            index++;
        }
        categoryIdMap.put("AGE", ageMap);
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

    public static BigInteger convertIdsToBitmask(Map<String, List<Integer>> categoryIds) {
        BigInteger bitmask = BigInteger.ZERO;

        for (Map.Entry<String, List<Integer>> entry : categoryIds.entrySet()) {
            String category = entry.getKey();
            List<Integer> ids = entry.getValue();
            Map<Integer, FeatureInfo> featureMap = categoryIdMap.get(category);

            if (featureMap != null) {
                for (Integer id : ids) {
                    FeatureInfo info = featureMap.get(id);
                    if (info != null) {
                        bitmask = bitmask.setBit(info.index());
                    }
                }
            }
        }

        return bitmask;
    }

    public static Map<String, List<Integer>> convertBitmaskToIds(BigInteger bitmask) {
        Map<String, List<Integer>> result = new HashMap<>();

        for (FeatureInfo info : featureInfoList) {
            if (bitmask.testBit(info.index())) {
                result.computeIfAbsent(info.category(), k -> new ArrayList<>())
                      .add(info.featureId());
            }
        }

        return result;
    }

    public static List<FeatureInfo> getFeatureInfosByIds(Map<String, List<Integer>> categoryIds) {
        List<FeatureInfo> result = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entry : categoryIds.entrySet()) {
            String category = entry.getKey();
            List<Integer> ids = entry.getValue();
            Map<Integer, FeatureInfo> featureMap = categoryIdMap.get(category);

            if (featureMap != null) {
                for (Integer id : ids) {
                    FeatureInfo info = featureMap.get(id);
                    if (info != null) {
                        result.add(info);
                    }
                }
            }
        }

        return result;
    }

    public static FeatureInfo getFeatureInfoByNameAndCategory(String name, String category) {
        return featureInfoList.stream()
                .filter(info -> info.name().equals(name) && info.category().equals(category))
                .findFirst()
                .orElse(null);
    }

    public record FeatureInfo(
            String category,
            String code,
            String name,
            int index,
            int featureId
    ) {
        // 기존 코드와의 호환성을 위한 생성자
        public FeatureInfo(String category, String code, String name, int index) {
            this(category, code, name, index, 0);
        }
    }
}