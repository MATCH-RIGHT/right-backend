package com.example.rightbackend.matching.filter.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class IdealFaceFeatureUtilTest {

    @Test
    @DisplayName("ID 맵을 비트마스크로 변환할 수 있다")
    void convertIdsToBitmaskTest() {
        // given
        Map<String, List<Integer>> categoryIds = new HashMap<>();
        categoryIds.put("FACE_SHAPE", Arrays.asList(1, 2));
        categoryIds.put("ANIMAL_LOOK", Arrays.asList(1));

        // when
        BigInteger bitmask = IdealFaceFeatureUtil.convertIdsToBitmask(categoryIds);

        // then
        assertNotEquals(BigInteger.ZERO, bitmask);
        // 변환된 비트마스크가 올바른 비트를 설정했는지 확인
        List<IdealFaceFeatureUtil.FeatureInfo> features = IdealFaceFeatureUtil.getFeatureInfosFromBitmask(bitmask);
        assertTrue(features.size() >= 3);
    }

    @Test
    @DisplayName("비트마스크를 ID 맵으로 변환할 수 있다")
    void convertBitmaskToIdsTest() {
        // given
        Map<String, List<Integer>> originalIds = new HashMap<>();
        originalIds.put("FACE_SHAPE", Arrays.asList(1, 2));
        originalIds.put("EYE_TYPE", Arrays.asList(1));
        BigInteger bitmask = IdealFaceFeatureUtil.convertIdsToBitmask(originalIds);

        // when
        Map<String, List<Integer>> convertedIds = IdealFaceFeatureUtil.convertBitmaskToIds(bitmask);

        // then
        assertTrue(convertedIds.containsKey("FACE_SHAPE"));
        assertTrue(convertedIds.containsKey("EYE_TYPE"));
        assertTrue(convertedIds.get("FACE_SHAPE").contains(1));
        assertTrue(convertedIds.get("FACE_SHAPE").contains(2));
        assertTrue(convertedIds.get("EYE_TYPE").contains(1));
    }

    @Test
    @DisplayName("ID로 FeatureInfo를 조회할 수 있다")
    void getFeatureInfosByIdsTest() {
        // given
        Map<String, List<Integer>> categoryIds = new HashMap<>();
        categoryIds.put("FACE_SHAPE", Arrays.asList(1));
        categoryIds.put("ANIMAL_LOOK", Arrays.asList(2));

        // when
        List<IdealFaceFeatureUtil.FeatureInfo> features = IdealFaceFeatureUtil.getFeatureInfosByIds(categoryIds);

        // then
        assertFalse(features.isEmpty());
        assertEquals(2, features.size());
        assertTrue(features.stream().anyMatch(f -> f.category().equals("FACE_SHAPE") && f.featureId() == 1));
        assertTrue(features.stream().anyMatch(f -> f.category().equals("ANIMAL_LOOK") && f.featureId() == 2));
    }

    @Test
    @DisplayName("빈 ID 맵은 빈 비트마스크를 반환한다")
    void emptyMapReturnZeroBitmask() {
        // given
        Map<String, List<Integer>> emptyIds = new HashMap<>();

        // when
        BigInteger bitmask = IdealFaceFeatureUtil.convertIdsToBitmask(emptyIds);

        // then
        assertEquals(BigInteger.ZERO, bitmask);
    }

    @Test
    @DisplayName("모든 Feature의 index는 고유하다")
    void allFeaturesHaveUniqueIndex() {
        // given & when
        List<IdealFaceFeatureUtil.FeatureInfo> allFeatures = IdealFaceFeatureUtil.getAllFeatureInfos();
        Set<Integer> indices = new HashSet<>();
        Set<Integer> featureIds = new HashSet<>();

        // then
        for (IdealFaceFeatureUtil.FeatureInfo feature : allFeatures) {
            assertTrue(indices.add(feature.index()), "Duplicate index found: " + feature.index());
        }
    }

    @Test
    @DisplayName("기존 호환성: 이름으로 Feature index를 찾을 수 있다")
    void getFeatureIndexByName() {
        // given
        String featureName = "LONG"; // FaceShape.LONG

        // when
        Integer index = IdealFaceFeatureUtil.getFeatureIndex(featureName);

        // then
        assertNotNull(index);
        assertTrue(index >= 0);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리의 ID는 무시된다")
    void invalidCategoryIsIgnored() {
        // given
        Map<String, List<Integer>> categoryIds = new HashMap<>();
        categoryIds.put("INVALID_CATEGORY", Arrays.asList(1, 2, 3));
        categoryIds.put("FACE_SHAPE", Arrays.asList(1));

        // when
        BigInteger bitmask = IdealFaceFeatureUtil.convertIdsToBitmask(categoryIds);
        Map<String, List<Integer>> convertedIds = IdealFaceFeatureUtil.convertBitmaskToIds(bitmask);

        // then
        assertFalse(convertedIds.containsKey("INVALID_CATEGORY"));
        assertTrue(convertedIds.containsKey("FACE_SHAPE"));
    }

    @Test
    @DisplayName("존재하지 않는 ID는 무시된다")
    void invalidFeatureIdIsIgnored() {
        // given
        Map<String, List<Integer>> categoryIds = new HashMap<>();
        categoryIds.put("FACE_SHAPE", Arrays.asList(1, 999)); // 999는 존재하지 않는 ID

        // when
        BigInteger bitmask = IdealFaceFeatureUtil.convertIdsToBitmask(categoryIds);
        List<IdealFaceFeatureUtil.FeatureInfo> features = IdealFaceFeatureUtil.getFeatureInfosFromBitmask(bitmask);

        // then
        // 유효한 ID(1)에 대한 feature만 포함
        assertTrue(features.stream().anyMatch(f -> f.featureId() == 1));
        assertFalse(features.stream().anyMatch(f -> f.featureId() == 999));
    }

    @Test
    @DisplayName("FeatureInfo는 이전 버전과 호환되는 생성자를 가진다")
    void featureInfoBackwardCompatibility() {
        // given & when
        IdealFaceFeatureUtil.FeatureInfo oldStyleInfo =
            new IdealFaceFeatureUtil.FeatureInfo("FACE_SHAPE", "LONG", "긴 얼굴형", 0);

        IdealFaceFeatureUtil.FeatureInfo newStyleInfo =
            new IdealFaceFeatureUtil.FeatureInfo("FACE_SHAPE", "LONG", "긴 얼굴형", 0, 1);

        // then
        assertEquals(oldStyleInfo.category(), newStyleInfo.category());
        assertEquals(oldStyleInfo.code(), newStyleInfo.code());
        assertEquals(oldStyleInfo.name(), newStyleInfo.name());
        assertEquals(oldStyleInfo.index(), newStyleInfo.index());
        assertEquals(0, oldStyleInfo.featureId()); // 기본값
        assertEquals(1, newStyleInfo.featureId());
    }
}