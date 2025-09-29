package com.example.rightbackend.rekognition.domain.Feature;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FeatureMapper {

    private static final Map<String, Class<?>> FEATURE_TYPE_MAP = new HashMap<>();

    static {
        FEATURE_TYPE_MAP.put("FACE_SHAPE", FaceShape.class);
        FEATURE_TYPE_MAP.put("ANIMAL_LOOK", AnimalLook.class);
        FEATURE_TYPE_MAP.put("AGE", Age.class);
        FEATURE_TYPE_MAP.put("BEARD", Beard.class);
        FEATURE_TYPE_MAP.put("EYE_SIZE", EyeSize.class);
        FEATURE_TYPE_MAP.put("EYE_TYPE", EyeType.class);
        FEATURE_TYPE_MAP.put("FOREHEAD", ForeHead.class);
        FEATURE_TYPE_MAP.put("GLASS", Glass.class);
        FEATURE_TYPE_MAP.put("JAW_SHAPE", JawShape.class);
        FEATURE_TYPE_MAP.put("LIP_SHAPE", LipShape.class);
        FEATURE_TYPE_MAP.put("NOSE_SHAPE", NoseShape.class);
        FEATURE_TYPE_MAP.put("SKIN_TONE", SkinTone.class);
    }

    public Integer getIdByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        try {
            if (FaceShape.fromName(name) != null) {
                return FaceShape.fromName(name).getId();
            }
        } catch (IllegalArgumentException e) {}

        try {
            if (AnimalLook.fromName(name) != null) {
                return AnimalLook.fromName(name).getId();
            }
        } catch (IllegalArgumentException e) {}

        try {
            if (Age.fromName(name) != null) {
                return Age.fromName(name).getId();
            }
        } catch (IllegalArgumentException e) {}

        try {
            if (Beard.fromName(name) != null) {
                return Beard.fromName(name).getId();
            }
        } catch (IllegalArgumentException e) {}

        try {
            if (EyeSize.fromName(name) != null) {
                return EyeSize.fromName(name).getId();
            }
        } catch (IllegalArgumentException e) {}

        try {
            if (EyeType.fromName(name) != null) {
                return EyeType.fromName(name).getId();
            }
        } catch (IllegalArgumentException e) {}

        try {
            if (ForeHead.fromName(name) != null) {
                return ForeHead.fromName(name).getId();
            }
        } catch (IllegalArgumentException e) {}

        try {
            if (Glass.fromName(name) != null) {
                return Glass.fromName(name).getId();
            }
        } catch (IllegalArgumentException e) {}

        try {
            if (JawShape.fromName(name) != null) {
                return JawShape.fromName(name).getId();
            }
        } catch (IllegalArgumentException e) {}

        try {
            if (LipShape.fromName(name) != null) {
                return LipShape.fromName(name).getId();
            }
        } catch (IllegalArgumentException e) {}

        try {
            if (NoseShape.fromName(name) != null) {
                return NoseShape.fromName(name).getId();
            }
        } catch (IllegalArgumentException e) {}

        try {
            if (SkinTone.fromName(name) != null) {
                return SkinTone.fromName(name).getId();
            }
        } catch (IllegalArgumentException e) {}

        return null;
    }

    public String getNameById(String featureType, Integer id) {
        if (featureType == null || id == null) {
            return null;
        }

        Class<?> enumClass = FEATURE_TYPE_MAP.get(featureType);
        if (enumClass == null) {
            return null;
        }

        try {
            if (enumClass == FaceShape.class) {
                return FaceShape.fromId(id).getName();
            } else if (enumClass == AnimalLook.class) {
                return AnimalLook.fromId(id).getName();
            } else if (enumClass == Age.class) {
                return Age.fromId(id).getName();
            } else if (enumClass == Beard.class) {
                return Beard.fromId(id).getName();
            } else if (enumClass == EyeSize.class) {
                return EyeSize.fromId(id).getName();
            } else if (enumClass == EyeType.class) {
                return EyeType.fromId(id).getName();
            } else if (enumClass == ForeHead.class) {
                return ForeHead.fromId(id).getName();
            } else if (enumClass == Glass.class) {
                return Glass.fromId(id).getName();
            } else if (enumClass == JawShape.class) {
                return JawShape.fromId(id).getName();
            } else if (enumClass == LipShape.class) {
                return LipShape.fromId(id).getName();
            } else if (enumClass == NoseShape.class) {
                return NoseShape.fromId(id).getName();
            } else if (enumClass == SkinTone.class) {
                return SkinTone.fromId(id).getName();
            }
        } catch (IllegalArgumentException e) {
            return null;
        }

        return null;
    }

    public String getFeatureTypeByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        try {
            FaceShape.fromName(name);
            return "FACE_SHAPE";
        } catch (IllegalArgumentException e) {}

        try {
            AnimalLook.fromName(name);
            return "ANIMAL_LOOK";
        } catch (IllegalArgumentException e) {}

        try {
            Age.fromName(name);
            return "AGE";
        } catch (IllegalArgumentException e) {}

        try {
            Beard.fromName(name);
            return "BEARD";
        } catch (IllegalArgumentException e) {}

        try {
            EyeSize.fromName(name);
            return "EYE_SIZE";
        } catch (IllegalArgumentException e) {}

        try {
            EyeType.fromName(name);
            return "EYE_TYPE";
        } catch (IllegalArgumentException e) {}

        try {
            ForeHead.fromName(name);
            return "FOREHEAD";
        } catch (IllegalArgumentException e) {}

        try {
            Glass.fromName(name);
            return "GLASS";
        } catch (IllegalArgumentException e) {}

        try {
            JawShape.fromName(name);
            return "JAW_SHAPE";
        } catch (IllegalArgumentException e) {}

        try {
            LipShape.fromName(name);
            return "LIP_SHAPE";
        } catch (IllegalArgumentException e) {}

        try {
            NoseShape.fromName(name);
            return "NOSE_SHAPE";
        } catch (IllegalArgumentException e) {}

        try {
            SkinTone.fromName(name);
            return "SKIN_TONE";
        } catch (IllegalArgumentException e) {}

        return null;
    }

    public List<Integer> convertNamesToIds(List<String> names) {
        List<Integer> ids = new ArrayList<>();
        for (String name : names) {
            Integer id = getIdByName(name);
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }

    public Map<String, List<Integer>> groupIdsByFeatureType(List<String> names) {
        Map<String, List<Integer>> grouped = new HashMap<>();

        for (String name : names) {
            String featureType = getFeatureTypeByName(name);
            Integer id = getIdByName(name);

            if (featureType != null && id != null) {
                grouped.computeIfAbsent(featureType, k -> new ArrayList<>()).add(id);
            }
        }

        return grouped;
    }
}