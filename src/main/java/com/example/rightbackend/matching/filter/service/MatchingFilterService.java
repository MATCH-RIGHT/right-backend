package com.example.rightbackend.matching.filter.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.global.config.resolver.Login;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.MatchingFilterError;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.matching.filter.controller.dto.request.MatchingFilterRequest;
import com.example.rightbackend.matching.filter.controller.dto.request.MatchingFilterIdsRequest;
import com.example.rightbackend.matching.filter.domain.MatchingFilter;
import com.example.rightbackend.matching.filter.domain.Region;
import com.example.rightbackend.matching.filter.domain.repository.MatchingFilterRepository;
import com.example.rightbackend.matching.filter.domain.repository.RegionRepository;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.domain.repository.MemberProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;

@Service
public class MatchingFilterService {

    private final MatchingFilterRepository matchingFilterRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final RegionRepository regionRepository;
    private final MemberRepository memberRepository;

    public MatchingFilterService(MatchingFilterRepository matchingFilterRepository, MemberProfileRepository memberProfileRepository, RegionRepository regionRepository, MemberRepository memberRepository) {
        this.matchingFilterRepository = matchingFilterRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.regionRepository = regionRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MatchingFilter getMatchingFilter(final LoginMember loginMember) {
        Member member = getMember(loginMember);
        return matchingFilterRepository.findByMemberProfile(member.getMemberProfile())
                .orElse(MatchingFilter.of(member.getMemberProfile()));
    }

    @Transactional
    public MatchingFilter createOrUpdateMatchingFilter(@Login LoginMember loginMember, MatchingFilterRequest request) {
        Member member = getMember(loginMember);
        MemberProfile memberProfile = member.getMemberProfile();

        if (request.minAge() != null && request.maxAge() != null && request.minAge() > request.maxAge()) {
            throw new RestApiException(MatchingFilterError.INVALID_AGE_RANGE);
        }

        Region region = null;
        if (request.regionId() != null) {
            region = regionRepository.findById(request.regionId())
                    .orElseThrow(() -> new RestApiException(MatchingFilterError.NULL_REGION));
        }

        Optional<MatchingFilter> existingFilterOpt = matchingFilterRepository.findByMemberProfile(memberProfile);
        MatchingFilter filter;

        if (existingFilterOpt.isPresent()) {
            filter = existingFilterOpt.get();
        } else {
            filter = MatchingFilter.of(memberProfile);
        }

        filter.setMinAge(request.minAge());
        filter.setMaxAge(request.maxAge());
        filter.setRegion(region);
        if (request.idealFaceFeatures() != null && !request.idealFaceFeatures().isEmpty()) {
            BigInteger idealFaceFeaturesBitmask = BigInteger.ZERO;
            
            for (String featureCode : request.idealFaceFeatures()) {
                Integer featureIndex = IdealFaceFeatureUtil.getFeatureIndex(featureCode);
                if (featureIndex == null) {
                    throw new RestApiException(MatchingFilterError.INVALID_FACE_FEATURE);
                }
                idealFaceFeaturesBitmask = idealFaceFeaturesBitmask.setBit(featureIndex);
            }
            
            filter.setIdealFaceFeaturesBitmask(idealFaceFeaturesBitmask);
        } else {
            filter.setIdealFaceFeaturesBitmask(BigInteger.ZERO);
        }

        return matchingFilterRepository.save(filter);
    }

    @Transactional
    public MatchingFilter createOrUpdateMatchingFilterWithIds(@Login LoginMember loginMember, MatchingFilterIdsRequest request) {
        Member member = getMember(loginMember);
        MemberProfile memberProfile = member.getMemberProfile();

        if (request.minAge() != null && request.maxAge() != null && request.minAge() > request.maxAge()) {
            throw new RestApiException(MatchingFilterError.INVALID_AGE_RANGE);
        }

        Region region = null;
        if (request.regionId() != null) {
            region = regionRepository.findById(request.regionId())
                    .orElseThrow(() -> new RestApiException(MatchingFilterError.NULL_REGION));
        }

        Optional<MatchingFilter> existingFilterOpt = matchingFilterRepository.findByMemberProfile(memberProfile);
        MatchingFilter filter;

        if (existingFilterOpt.isPresent()) {
            filter = existingFilterOpt.get();
        } else {
            filter = MatchingFilter.of(memberProfile);
        }

        filter.setMinAge(request.minAge());
        filter.setMaxAge(request.maxAge());
        filter.setRegion(region);

        if (request.idealFaceFeatureIds() != null && !request.idealFaceFeatureIds().isEmpty()) {
            BigInteger idealFaceFeaturesBitmask = IdealFaceFeatureUtil.convertIdsToBitmask(request.idealFaceFeatureIds());
            filter.setIdealFaceFeaturesBitmask(idealFaceFeaturesBitmask);
        } else {
            filter.setIdealFaceFeaturesBitmask(BigInteger.ZERO);
        }

        return matchingFilterRepository.save(filter);
    }

    private Member getMember(final LoginMember loginMember) {
        return memberRepository.findById(loginMember.memberId()).orElseThrow(()
                -> new RestApiException(MemberError.NULL_MEMBER));
    }
}