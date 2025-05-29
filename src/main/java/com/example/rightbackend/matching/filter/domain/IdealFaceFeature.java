package com.example.rightbackend.matching.filter.domain;

import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.rekognition.domain.FaceFeature;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "idealFaceFeature")
public class IdealFaceFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdealFaceFeatureId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memberProfileId")
    private MemberProfile memberProfile;

    @ManyToOne
    @JoinColumn(name = "faceFeatureName")
    private FaceFeature faceFeature;

    protected IdealFaceFeature() {}

    public static IdealFaceFeature of(MemberProfile memberProfile, FaceFeature faceFeature) {
        IdealFaceFeature link = new IdealFaceFeature();
        link.memberProfile = memberProfile;
        link.faceFeature = faceFeature;
        return link;
    }
}