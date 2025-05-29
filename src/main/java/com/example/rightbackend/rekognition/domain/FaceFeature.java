package com.example.rightbackend.rekognition.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter @Getter
@Table(name = "faceFeature")
public class FaceFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "faceFeatureName")
    private String name;

    @OneToMany(mappedBy = "faceFeature", cascade = CascadeType.ALL)
    private List<MyFaceFeature> myFaceFeatures = new ArrayList<>();

    @OneToMany(mappedBy = "faceFeature", cascade = CascadeType.ALL)
    private List<MyFaceFeature> idealFaceFeatures = new ArrayList<>();

    protected FaceFeature() {}

    public static FaceFeature of(String name) {
        FaceFeature feature = new FaceFeature();
        feature.name = name;
        return feature;
    }
}