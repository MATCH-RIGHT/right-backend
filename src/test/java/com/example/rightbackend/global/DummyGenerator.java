package com.example.rightbackend.global;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.Token;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.auth.domain.repository.TokenRepository;
import com.example.rightbackend.auth.service.TokenProvider;
import com.example.rightbackend.member.controller.dto.EncodeMember;
import com.example.rightbackend.member.controller.dto.EncodeMemberProfile;
import com.example.rightbackend.member.domain.Interest;
import com.example.rightbackend.member.domain.Location;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.domain.MemberProfileToInterest;
import com.example.rightbackend.member.domain.repository.InterestRepository;
import com.example.rightbackend.member.domain.repository.LocationRepository;
import com.example.rightbackend.member.domain.repository.MemberProfileRepository;
import com.example.rightbackend.member.service.TextEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DummyGenerator {
    @Autowired MemberRepository memberRepository;
    @Autowired MemberProfileRepository memberProfileRepository;
    @Autowired InterestRepository interestRepository;
    @Autowired LocationRepository locationRepository;
    @Autowired TextEncoder textEncoder;

    public static final String GIVEN_NAME = "tmpName";
    public static final String GIVEN_PROVIDER = "tmpProvider";
    public static final String GIVEN_PROVIDER_ID = "tmpID";
    public static final String GIVEN_PASSWORD = "tmpPassword";
    public static final String GIVEN_PHONE_NUMBER = "010-1234-5678";
    public static final String GIVEN_NICKNAME = "tmpNicknmae";
    public static final String GIVEN_GENDER = "0";
    public static final String GIVEN_BIRTHDAY = "1999-01-01";
    public static final String GIVEN_LOCATION_NAME = "부산광역시 남구";
    public static final String GIVEN_HEIGHT = "180";
    public static final String GIVEN_BODY_TYPE = "slim";
    public static final String GIVEN_JOB = "student";
    public static final String GIVEN_MONEY = "0";
    public static final String GIVEN_MYSELF = "hello world";

    public static final List<String> GIVEN_INTERESTS = List.of("READING", "TRAVELING", "CODING");
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private TokenRepository tokenRepository;

    public Member generateSingleMember() {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        Member member = makeMember(uniqueSuffix);
        saveMemberProfile(member);
        return member;
    }

    private Member makeMember(String uniqueSuffix) {
        EncodeMember signUp = new EncodeMember(
                GIVEN_NAME + uniqueSuffix, 
                GIVEN_PROVIDER, 
                GIVEN_PROVIDER_ID + uniqueSuffix, 
                GIVEN_PASSWORD,
                GIVEN_PHONE_NUMBER + uniqueSuffix);
        return memberRepository.save(Member.of(signUp));
    }

    private void saveMemberProfile(final Member member) {
        EncodeMemberProfile encodeMemberProfile = new EncodeMemberProfile(
                TextEncoder.encrypt(GIVEN_NICKNAME),
                TextEncoder.encrypt(GIVEN_GENDER),
                TextEncoder.encrypt(GIVEN_BIRTHDAY),
                TextEncoder.encrypt(GIVEN_HEIGHT),
                TextEncoder.encrypt(GIVEN_BODY_TYPE),
                TextEncoder.encrypt(GIVEN_JOB),
                GIVEN_INTERESTS,
                TextEncoder.encrypt(GIVEN_MYSELF),
                TextEncoder.encrypt(GIVEN_MONEY));

        MemberProfile memberProfile = MemberProfile.of(encodeMemberProfile, member);

        addInterestsToMemberProfile(memberProfile, GIVEN_INTERESTS);

        // 지역 추가
        Location location = locationRepository.findByName(GIVEN_LOCATION_NAME)
                .orElseGet(() -> locationRepository.save(Location.of(GIVEN_LOCATION_NAME)));
        memberProfile.addLocation(location);

        memberProfileRepository.save(memberProfile);
        member.setMemberProfile(memberProfile);
        memberRepository.save(member);
    }

    private void addInterestsToMemberProfile(MemberProfile memberProfile, List<String> interests) {
        if(interests == null || interests.isEmpty()) {
            return;
        }
        for(String interestName: interests) {
            Interest interest = interestRepository.findByName(interestName).orElseGet(() -> interestRepository.save(Interest.of(interestName)));
            memberProfile.addInterest(interest);
        }
    }

    public String generateAccessToken(Member member) {
        String accessToken = tokenProvider.generateAccessToken(member.getLoginMember());
        Token refreshToken = new Token(member);
        refreshToken.setRefreshToken(accessToken);
        tokenRepository.save(refreshToken);
        return accessToken;
    }
}