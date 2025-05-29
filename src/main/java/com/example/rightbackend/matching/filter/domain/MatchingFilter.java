package com.example.rightbackend.matching.filter.domain;

import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.MatchingFilterError;
import com.example.rightbackend.member.domain.MemberProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Getter @Setter
@Table(name = "matching_filter", indexes = {
    @Index(name = "idx_matching_filter_gender_partition", columnList = "gender_partition"),
    @Index(name = "idx_matching_filter_region_partition", columnList = "region_partition"),
    @Index(name = "idx_matching_filter_combined_partition", columnList = "gender_partition,region_partition")
})
public class MatchingFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_filter_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_profile_id")
    private MemberProfile memberProfile;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "ideal_face_features_bitmask", columnDefinition = "VARBINARY(255)")
    private BigInteger idealFaceFeaturesBitmask = BigInteger.ZERO;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;
    
    @Column(name = "gender_partition", length = 10)
    private String genderPartition;
    
    @Column(name = "region_partition", length = 20)
    private String regionPartition;

    protected MatchingFilter() {}

    public static MatchingFilter of(MemberProfile memberProfile) {
        MatchingFilter filter = new MatchingFilter();
        filter.memberProfile = memberProfile;
        filter.genderPartition = memberProfile.getGender();
        return filter;
    }
    
    public void setRegion(Region region) {
        this.region = region;
        if (region != null) {
            this.regionPartition = region.getCode();
        } else {
            this.regionPartition = null;
        }
    }

    public void addIdealFaceFeatureToBitmask(int featureIndex) {
        if (featureIndex < 0) {
            throw new RestApiException(MatchingFilterError.NEGATIVE_FEATURE_INDEX);
        }
        this.idealFaceFeaturesBitmask = this.idealFaceFeaturesBitmask.setBit(featureIndex);
    }

    public void removeIdealFaceFeatureFromBitmask(int featureIndex) {
        if (featureIndex < 0) {
            throw new RestApiException(MatchingFilterError.NEGATIVE_FEATURE_INDEX);
        }
        this.idealFaceFeaturesBitmask = this.idealFaceFeaturesBitmask.clearBit(featureIndex);
    }

    public boolean hasIdealFaceFeature(int featureIndex) {
        if (featureIndex < 0) {
            throw new RestApiException(MatchingFilterError.NEGATIVE_FEATURE_INDEX);
        }
        return this.idealFaceFeaturesBitmask.testBit(featureIndex);
    }
}
