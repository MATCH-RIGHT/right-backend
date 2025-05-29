package com.example.rightbackend.rekognition.domain.repository;

import com.example.rightbackend.rekognition.domain.FaceFeature;
import com.example.rightbackend.rekognition.domain.MyFaceFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyFaceFeatureRepository extends JpaRepository<MyFaceFeature, Long> {
}