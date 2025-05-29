package com.example.rightbackend.rekognition.domain.repository;

import com.example.rightbackend.rekognition.domain.FaceFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FaceFeatureRepository extends JpaRepository<FaceFeature, Long> {
    Optional<FaceFeature> findByName(String name);
    List<FaceFeature> findAllByOrderByIdAsc();
}