package com.example.rightbackend.global.config.loader;

import com.example.rightbackend.rekognition.domain.FaceFeature;
import com.example.rightbackend.rekognition.domain.Feature.*;
import com.example.rightbackend.rekognition.domain.repository.FaceFeatureRepository;
import com.example.rightbackend.rekognition.service.FaceFeatureBitmaskUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
public class FaceFeatureDataLoader {

    private final FaceFeatureRepository faceFeatureRepository;
    private final FaceFeatureBitmaskUtil bitmaskUtil;
    
    public FaceFeatureDataLoader(FaceFeatureRepository faceFeatureRepository, FaceFeatureBitmaskUtil bitmaskUtil) {
        this.faceFeatureRepository = faceFeatureRepository;
        this.bitmaskUtil = bitmaskUtil;
    }

    @Bean(name = "faceFeatureDataLoaderRunner")
    public CommandLineRunner initFaceFeatureData() {
        return args -> {
            loadFaceFeatureData();
            bitmaskUtil.initializeBitmaskIndices();
            int totalFeatureCount = bitmaskUtil.getTotalFeatureCount();
        };
    }
    
    public void loadFaceFeatureData() {
        List<String> faceFeatureNames = new ArrayList<>();
        
        // Age
        Arrays.stream(Age.values()).forEach(age -> faceFeatureNames.add(age.getName()));
        
        // AnimalLook
        Arrays.stream(AnimalLook.values()).forEach(animalLook -> faceFeatureNames.add(animalLook.getName()));
        
        // Beard
        Arrays.stream(Beard.values()).forEach(beard -> faceFeatureNames.add(beard.getName()));
        
        // EyeSize
        Arrays.stream(EyeSize.values()).forEach(eyeSize -> faceFeatureNames.add(eyeSize.getName()));
        
        // EyeType
        Arrays.stream(EyeType.values()).forEach(eyeType -> faceFeatureNames.add(eyeType.getName()));
        
        // FaceShape
        Arrays.stream(FaceShape.values()).forEach(faceShape -> faceFeatureNames.add(faceShape.getName()));
        
        // ForeHead
        Arrays.stream(ForeHead.values()).forEach(foreHead -> faceFeatureNames.add(foreHead.getName()));
        
        // Glass
        Arrays.stream(Glass.values()).forEach(glass -> faceFeatureNames.add(glass.getName()));
        
        // JawShape
        Arrays.stream(JawShape.values()).forEach(jawShape -> faceFeatureNames.add(jawShape.getName()));
        
        // LipShape
        Arrays.stream(LipShape.values()).forEach(lipShape -> faceFeatureNames.add(lipShape.getName()));
        
        // NoseShape
        Arrays.stream(NoseShape.values()).forEach(noseShape -> faceFeatureNames.add(noseShape.getName()));
        
        // SkinTone
        Arrays.stream(SkinTone.values()).forEach(skinTone -> faceFeatureNames.add(skinTone.getName()));

        for (String name : faceFeatureNames) {
            Optional<FaceFeature> existingFeature = faceFeatureRepository.findByName(name);
            if (existingFeature.isEmpty()) {
                FaceFeature faceFeature = FaceFeature.of(name);
                faceFeatureRepository.save(faceFeature);
            }
        }
    }
}