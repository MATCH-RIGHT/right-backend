package com.example.rightbackend.member.service;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.member.controller.dto.EncodeMember;
import com.example.rightbackend.member.controller.dto.EncodeMemberProfile;
import com.example.rightbackend.member.controller.dto.request.SignUpRequest;
import com.example.rightbackend.member.controller.dto.response.MemberResponse;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.domain.repository.MemberProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberProfileService {

    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;

    public MemberProfileService(MemberRepository memberRepository, final MemberProfileRepository memberProfileRepository) {
        this.memberRepository = memberRepository;
        this.memberProfileRepository = memberProfileRepository;
    }

    @Transactional
    public String signUp(final SignUpRequest request) {

        Member member = saveMemberEntity(request);

        MemberProfile memberProfile = getMemberProfileEntity(request, member);

        memberProfile = memberProfileRepository.save(memberProfile);

        member.setMemberProfile(memberProfile);

        return MemberResponse.SIGN_UP_SUCCESS.getMessage();
    }

        private Member saveMemberEntity(final SignUpRequest request) {
        EncodeMember encodeMember = encodeForMember(request);
        return memberRepository.save(Member.of(encodeMember));
    }

    private EncodeMember encodeForMember(final SignUpRequest request) {
        return new EncodeMember(request.provider(),
                request.providerId(),
                request.password(),
                request.phoneNumber());
    }

    private MemberProfile getMemberProfileEntity(final SignUpRequest request, final Member member) {
        EncodeMemberProfile encodeProfile = encodeForMemberProfile(request);
        return MemberProfile.of(encodeProfile, member);
    }

    private EncodeMemberProfile encodeForMemberProfile(final SignUpRequest request) {
        return new EncodeMemberProfile(
                TextEncoder.encrypt(request.nickname()),
                TextEncoder.encrypt(request.gender()),
                TextEncoder.encrypt(request.birthday()),
                TextEncoder.encrypt(request.address()),
                TextEncoder.encrypt(request.height()),
                TextEncoder.encrypt(request.body_type()),
                TextEncoder.encrypt(request.job()),
                TextEncoder.encrypt(request.myself()),
                TextEncoder.encrypt(request.gender()));
    }
}