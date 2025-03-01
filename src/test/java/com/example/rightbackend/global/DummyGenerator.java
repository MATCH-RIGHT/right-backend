package com.example.rightbackend.global;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.member.controller.dto.EncodeMember;
import com.example.rightbackend.member.controller.dto.EncodeMemberProfile;
import com.example.rightbackend.member.controller.dto.EncodeSignUp;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.domain.repository.MemberProfileRepository;
import com.example.rightbackend.member.service.TextEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DummyGenerator {
    @Autowired MemberRepository memberRepository;
    @Autowired MemberProfileRepository memberProfileRepository;
    @Autowired TextEncoder textEncoder;

    public static final String GIVEN_PROVIDER = "tmp";
    public static final String GIVEN_PROVIDER_ID = "tmpID";
    public static final String GIVEN_PASSWORD = "tmpPassword";
    public static final String GIVEN_PHONE_NUMBER = "010-1234-5678";
    public static final String GIVEN_NICKNAME = "tmpNicknmae";
    public static final String GIVEN_GENDER = "0";
    public static final String GIVEN_BIRTHDAY = "1999-01-01";
    public static final String GIVEN_ADDRESS = "busan";
    public static final String GIVEN_HEIGHT = "180";
    public static final String GIVEN_BODY_TYPE = "slim";
    public static final String GIVEN_JOB = "student";
    public static final String GIVEN_MONEY = "0";
    public static final String GIVEN_MYSELF = "hello world";

    public Member generateSingleMember() {
        Member member = makeMember();
        saveMemberProfile(member);
        return member;
    }

    private Member makeMember() {
        EncodeMember signUp = new EncodeMember(GIVEN_PROVIDER, GIVEN_PROVIDER_ID, GIVEN_PASSWORD,GIVEN_PHONE_NUMBER);
        return memberRepository.save(Member.of(signUp));
    }

    private void saveMemberProfile(final Member member) {
        EncodeMemberProfile encodeMemberProfile = new EncodeMemberProfile(
                TextEncoder.encrypt(GIVEN_NICKNAME),
                TextEncoder.encrypt(GIVEN_GENDER),
                TextEncoder.encrypt(GIVEN_BIRTHDAY),
                TextEncoder.encrypt(GIVEN_ADDRESS),
                TextEncoder.encrypt(GIVEN_HEIGHT),
                TextEncoder.encrypt(GIVEN_BODY_TYPE),
                TextEncoder.encrypt(GIVEN_JOB),
                TextEncoder.encrypt(GIVEN_MONEY),
                TextEncoder.encrypt(GIVEN_MYSELF));

        MemberProfile memberProfile = MemberProfile.of(encodeMemberProfile, member);
        memberProfileRepository.save(memberProfile);
        member.setMemberProfile(memberProfile);
        memberRepository.save(member);
    }

}