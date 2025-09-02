package com.example.rightbackend.member.domain;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.member.controller.dto.EncodeMemberPage;
import com.example.rightbackend.member.controller.dto.EncodeMemberProfile;
import com.example.rightbackend.member.controller.dto.response.MemberPageResponse;
import com.example.rightbackend.member.service.TextEncoder;
import com.example.rightbackend.rekognition.domain.FaceFeature;
import com.example.rightbackend.rekognition.domain.MyFaceFeature;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.MemberError;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "memberProfile")
@Getter @Setter
public class MemberProfile implements Persistable<Long> {

    @Id
    @Column(name = "memberProfileId")
    private Long id;

    @Column
    private String nickname;

    @Column
    private String gender;

    @Column
    private String birthday;

    @Column
    private String height;

    @Column
    private String body_type;

    @Column
    private String job;

    @Column
    private String money = "0";

    @Column
    private String myself;

    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberProfileToLocation> memberProfileToLocations = new ArrayList<>();


    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberProfileToInterest> memberProfileToInterests = new ArrayList<>();

    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyFaceFeature> myFaceFeatures = new ArrayList<>();
    
    @Column(name = "face_features_bitmask")
    private BigInteger faceFeaturesBitmask = BigInteger.ZERO;
    
    @OneToOne(mappedBy = "memberProfile")
    private Member member;

    protected MemberProfile() {
    }

    public static MemberProfile of(EncodeMemberProfile request, Member member) {
        MemberProfile memberProfile = new MemberProfile();
        memberProfile.id = member.getId();
        memberProfile.nickname = request.nickname();
        memberProfile.gender = request.gender();
        memberProfile.birthday = request.birthday();
        memberProfile.height = request.height();
        memberProfile.body_type = request.body_type();
        memberProfile.job = request.job();
        memberProfile.myself = request.myself();
        memberProfile.member = member;
        return memberProfile;
    }

    public EncodeMemberPage getMemberPage() {
        List<MemberPageResponse.InterestDto> interests = memberProfileToInterests.stream()
                .map(link -> new MemberPageResponse.InterestDto(link.getInterest().getId(), link.getInterest().getName()))
                .collect(Collectors.toList());
        
        String locationName = null;
        if (!memberProfileToLocations.isEmpty()) {
            locationName = memberProfileToLocations.get(0).getLocation().getName();
        }
        
        return new EncodeMemberPage(nickname, locationName, height, body_type, job, interests, myself);
    }

    public void addInterest(Interest interest) {
        MemberProfileToInterest link = MemberProfileToInterest.of(this, interest);
        this.memberProfileToInterests.add(link);
    }
    
    public void addLocation(Location location) {
        MemberProfileToLocation link = MemberProfileToLocation.of(this, location);
        this.memberProfileToLocations.add(link);
    }

    public List<String> getFaceAnalysisResponse() {
        return myFaceFeatures.stream()
                .map(feature -> feature.getFaceFeature().getName())
                .collect(Collectors.toList());
    }

    public void addFaceFeatureToBitmask(FaceFeature faceFeature, int featureIndex) {
        if (featureIndex < 0) {
            throw new RestApiException(MemberError.INVALID_FEATURE_INDEX);
        }
        this.faceFeaturesBitmask = this.faceFeaturesBitmask.setBit(featureIndex);
    }

    public void removeFaceFeatureFromBitmask(int featureIndex) {
        if (featureIndex < 0) {
            throw new RestApiException(MemberError.INVALID_FEATURE_INDEX);
        }
        this.faceFeaturesBitmask = this.faceFeaturesBitmask.clearBit(featureIndex);
    }

    public boolean hasFaceFeature(int featureIndex) {
        if (featureIndex < 0) {
            throw new RestApiException(MemberError.INVALID_FEATURE_INDEX);
        }
        
        return this.faceFeaturesBitmask.testBit(featureIndex);
    }

    public Integer getAge() {
        if (birthday == null || birthday.isEmpty()) {
            return null;
        }
        
        try {
            String decryptedBirthday = TextEncoder.decrypt(birthday);
            int birthYear = Integer.parseInt(decryptedBirthday.substring(0, 4));
            int currentYear = java.time.LocalDate.now().getYear();
            return currentYear - birthYear + 1;
        } catch (Exception e) {
            return null;
        }
    }
    
    // 복호화된 값을 반환하는 메서드들
    public String getDecryptedNickname() {
        return nickname != null ? TextEncoder.decrypt(nickname) : "";
    }
    
    public String getDecryptedGender() {
        return gender != null ? TextEncoder.decrypt(gender) : "";
    }
    
    public String getDecryptedBirthday() {
        return birthday != null ? TextEncoder.decrypt(birthday) : "";
    }
    
    public String getDecryptedHeight() {
        return height != null ? TextEncoder.decrypt(height) : "";
    }
    
    public String getDecryptedBodyType() {
        return body_type != null ? TextEncoder.decrypt(body_type) : "";
    }
    
    public String getDecryptedJob() {
        return job != null ? TextEncoder.decrypt(job) : "";
    }
    
    public String getDecryptedMyself() {
        return myself != null ? TextEncoder.decrypt(myself) : "";
    }
    
    public String getLocationPartition() {
        if (memberProfileToLocations == null || memberProfileToLocations.isEmpty()) {
            return null;
        }
        
        return memberProfileToLocations.get(0).getLocation().getName();
    }
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}