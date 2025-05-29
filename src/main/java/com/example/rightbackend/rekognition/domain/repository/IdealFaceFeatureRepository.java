package com.example.rightbackend.rekognition.domain.repository;

import com.example.rightbackend.matching.filter.domain.IdealFaceFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdealFaceFeatureRepository extends JpaRepository<IdealFaceFeature, Long> {
}