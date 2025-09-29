package com.example.rightbackend.rekognition.domain.Feature;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FeatureMapperTest {

    private FeatureMapper featureMapper;

    @BeforeEach
    void setUp() {
        featureMapper = new FeatureMapper();
    }

    @Test
    @DisplayName("얼굴형 이름으로 ID를 가져올 수 있다")
    void getIdByName_FaceShape() {
        // when
        Integer id = featureMapper.getIdByName("긴 얼굴형");

        // then
        assertEquals(1, id);
    }

    @Test
    @DisplayName("동물상 이름으로 ID를 가져올 수 있다")
    void getIdByName_AnimalLook() {
        // when
        Integer id = featureMapper.getIdByName("고양이상");

        // then
        assertEquals(1, id);
    }

    @Test
    @DisplayName("존재하지 않는 이름은 null을 반환한다")
    void getIdByName_InvalidName() {
        // when
        Integer id = featureMapper.getIdByName("존재하지않는특징");

        // then
        assertNull(id);
    }

    @Test
    @DisplayName("Feature 타입과 ID로 이름을 가져올 수 있다")
    void getNameById() {
        // when
        String name = featureMapper.getNameById("FACE_SHAPE", 1);

        // then
        assertEquals("긴 얼굴형", name);
    }

    @Test
    @DisplayName("잘못된 ID는 null을 반환한다")
    void getNameById_InvalidId() {
        // when
        String name = featureMapper.getNameById("FACE_SHAPE", 999);

        // then
        assertNull(name);
    }

    @Test
    @DisplayName("이름으로 Feature 타입을 가져올 수 있다")
    void getFeatureTypeByName() {
        // when
        String type = featureMapper.getFeatureTypeByName("긴 얼굴형");

        // then
        assertEquals("FACE_SHAPE", type);
    }

    @Test
    @DisplayName("이름 리스트를 ID 리스트로 변환할 수 있다")
    void convertNamesToIds() {
        // given
        List<String> names = Arrays.asList("긴 얼굴형", "고양이상", "동안");

        // when
        List<Integer> ids = featureMapper.convertNamesToIds(names);

        // then
        assertEquals(3, ids.size());
        assertTrue(ids.contains(1)); // 긴 얼굴형
        assertTrue(ids.contains(1)); // 고양이상
        assertTrue(ids.contains(1)); // 동안
    }

    @Test
    @DisplayName("Feature 이름들을 타입별로 그룹화하여 ID로 변환할 수 있다")
    void groupIdsByFeatureType() {
        // given
        List<String> names = Arrays.asList(
            "긴 얼굴형", "계란형",  // FACE_SHAPE
            "고양이상", "강아지상",  // ANIMAL_LOOK
            "동안"  // AGE
        );

        // when
        Map<String, List<Integer>> grouped = featureMapper.groupIdsByFeatureType(names);

        // then
        assertEquals(3, grouped.size());
        assertTrue(grouped.containsKey("FACE_SHAPE"));
        assertTrue(grouped.containsKey("ANIMAL_LOOK"));
        assertTrue(grouped.containsKey("AGE"));

        assertEquals(2, grouped.get("FACE_SHAPE").size());
        assertEquals(2, grouped.get("ANIMAL_LOOK").size());
        assertEquals(1, grouped.get("AGE").size());
    }

    @Test
    @DisplayName("모든 Feature enum의 fromId와 fromName 메서드가 정상 작동한다")
    void testAllEnumsFromMethods() {
        // FaceShape
        assertEquals(FaceShape.LONG, FaceShape.fromId(1));
        assertEquals(FaceShape.LONG, FaceShape.fromName("긴 얼굴형"));

        // AnimalLook
        assertEquals(AnimalLook.CAT, AnimalLook.fromId(1));
        assertEquals(AnimalLook.CAT, AnimalLook.fromName("고양이상"));

        // Age
        assertEquals(Age.YOUNG, Age.fromId(1));
        assertEquals(Age.YOUNG, Age.fromName("동안"));

        // Beard
        assertEquals(Beard.BEARDED, Beard.fromId(1));
        assertEquals(Beard.BEARDED, Beard.fromName("수염 있는"));

        // EyeSize
        assertEquals(EyeSize.SMALL, EyeSize.fromId(1));
        assertEquals(EyeSize.SMALL, EyeSize.fromName("작은"));

        // EyeType
        assertEquals(EyeType.UPTURNED, EyeType.fromId(1));
        assertEquals(EyeType.UPTURNED, EyeType.fromName("눈꼬리가 올라간 눈"));

        // ForeHead
        assertEquals(ForeHead.NARROW, ForeHead.fromId(1));
        assertEquals(ForeHead.NARROW, ForeHead.fromName("좁은 이마"));

        // Glass
        assertEquals(Glass.GLASSED, Glass.fromId(1));
        assertEquals(Glass.GLASSED, Glass.fromName("안경을 쓴"));

        // JawShape
        assertEquals(JawShape.SHARP, JawShape.fromId(1));
        assertEquals(JawShape.SHARP, JawShape.fromName("날카로운 턱선"));

        // LipShape
        assertEquals(LipShape.THICK, LipShape.fromId(1));
        assertEquals(LipShape.THICK, LipShape.fromName("두꺼운 입술"));

        // NoseShape
        assertEquals(NoseShape.SMART, NoseShape.fromId(1));
        assertEquals(NoseShape.SMART, NoseShape.fromName("오똑한"));

        // SkinTone
        assertEquals(SkinTone.BRIGHT, SkinTone.fromId(1));
        assertEquals(SkinTone.BRIGHT, SkinTone.fromName("밝은 피부 색"));
    }

    @Test
    @DisplayName("잘못된 ID로 enum 변환시 예외가 발생한다")
    void testInvalidIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> FaceShape.fromId(999));
        assertThrows(IllegalArgumentException.class, () -> AnimalLook.fromId(999));
    }

    @Test
    @DisplayName("잘못된 이름으로 enum 변환시 예외가 발생한다")
    void testInvalidNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> FaceShape.fromName("존재하지않는이름"));
        assertThrows(IllegalArgumentException.class, () -> AnimalLook.fromName("존재하지않는이름"));
    }
}