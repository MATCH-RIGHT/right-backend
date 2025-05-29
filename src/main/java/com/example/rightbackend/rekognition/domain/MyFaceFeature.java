package com.example.rightbackend.rekognition.domain;

import com.example.rightbackend.member.domain.MemberProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "myFaceFeature")
public class MyFaceFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MyFaceFeatureId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memberProfileId")
    private MemberProfile memberProfile;

    @ManyToOne
    @JoinColumn(name = "faceFeatureName")
    private FaceFeature faceFeature;

    protected MyFaceFeature() {}

    public static MyFaceFeature of(MemberProfile memberProfile, FaceFeature faceFeature) {
        MyFaceFeature link = new MyFaceFeature();
        link.memberProfile = memberProfile;
        link.faceFeature = faceFeature;
        return link;
    }
}