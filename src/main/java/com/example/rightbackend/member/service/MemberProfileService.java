package com.example.rightbackend.member.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.Token;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.auth.domain.repository.TokenRepository;
import com.example.rightbackend.auth.service.PasswordEncoder;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.global.response.error.TokenError;
import com.example.rightbackend.member.controller.dto.EncodeMember;
import com.example.rightbackend.member.controller.dto.EncodeMemberProfile;
import com.example.rightbackend.member.controller.dto.request.CheckIdRequest;
import com.example.rightbackend.member.controller.dto.request.ResetPasswordRequest;
import com.example.rightbackend.member.controller.dto.request.SearchIdRequest;
import com.example.rightbackend.member.controller.dto.request.SignUpRequest;
import com.example.rightbackend.member.controller.dto.response.MemberPageResponse;
import com.example.rightbackend.member.controller.dto.response.MemberResponse;
import com.example.rightbackend.member.domain.Interest;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.domain.MemberProfileToInterest;
import com.example.rightbackend.member.domain.repository.InterestRepository;
import com.example.rightbackend.member.domain.repository.MemberProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberProfileService {

    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final InterestRepository interestRepository;

    public MemberProfileService(PasswordEncoder passwordEncoder, TokenRepository tokenRepository, MemberRepository memberRepository, final MemberProfileRepository memberProfileRepository, InterestRepository interestRepository) {
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.memberRepository = memberRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.interestRepository = interestRepository;
    }

    @Transactional
    public String signUp(final SignUpRequest request) {

        Member member = saveMemberEntity(request);

        MemberProfile memberProfile = getMemberProfileEntity(request, member);

        addInterestsToMemberProfile(memberProfile, request.interests());

        memberProfile = memberProfileRepository.save(memberProfile);

        member.setMemberProfile(memberProfile);

        return MemberResponse.SIGN_UP_SUCCESS.getMessage();
    }

    private void addInterestsToMemberProfile(MemberProfile memberProfile, List<String> interests) {
        if(interests == null || interests.isEmpty()) {
            return;
        }
        for(String interestName: interests) {
            Interest interest = interestRepository.findByName(interestName).orElseGet(() -> interestRepository.save(Interest.of(interestName)));
            MemberProfileToInterest link = MemberProfileToInterest.of(memberProfile, interest);
            memberProfile.getMemberProfileToInterests().add(link);
        }
    }

    private Member saveMemberEntity(final SignUpRequest request) {
        EncodeMember encodeMember = encodeForMember(request);
        return memberRepository.save(Member.of(encodeMember));
    }

    private EncodeMember encodeForMember(final SignUpRequest request) {
        return new EncodeMember(request.name(),
                request.provider(),
                request.providerId(),
                passwordEncoder.encrypt(request.password()),
                TextEncoder.encrypt(request.phoneNumber()));
    }

    private MemberProfile getMemberProfileEntity(final SignUpRequest request, final Member member) {
        EncodeMemberProfile encodeProfile = encodeForMemberProfile(request);
        return MemberProfile.of(encodeProfile, member);
    }

    private EncodeMemberProfile encodeForMemberProfile(final SignUpRequest request) {
        return new EncodeMemberProfile(TextEncoder.encrypt(request.nickname()),
                TextEncoder.encrypt(request.gender()),
                TextEncoder.encrypt(request.birthday()),
                TextEncoder.encrypt(request.address()),
                TextEncoder.encrypt(request.height()),
                TextEncoder.encrypt(request.body_type()),
                TextEncoder.encrypt(request.job()),
                request.interests(),
                request.myself(),
                TextEncoder.encrypt(request.gender()));
    }

    @Transactional(readOnly = true)
    public String checkDuplicateId(final CheckIdRequest providerId) {
        if(memberRepository.findByProviderId(String.valueOf(providerId)).isPresent()) {
            throw new RestApiException(MemberError.DUPLICATE_ID);
        }
        return MemberResponse.AVAILABLE_ID.getMessage();
    }

    @Transactional(readOnly = true)
    public MemberPageResponse getMemberPage(final LoginMember loginMember) {
        Member member = getMember(loginMember);
        return member.getMemberPageResopnse();
    }

    @Transactional
    public String searchId(final SearchIdRequest searchIdRequest) {
        Member member = memberRepository.findFirstByNameAndPhoneNumber(searchIdRequest.name(), searchIdRequest.phoneNumber()).orElseThrow(()
                -> new RestApiException(MemberError.NULL_MEMBER));
        return member.getProviderId();
    }

    @Transactional
    public String resetPassword(final ResetPasswordRequest resetPasswordRequest) {
        Member member = memberRepository.findFirstByNameAndPhoneNumber(resetPasswordRequest.name(), resetPasswordRequest.phoneNumber()).orElseThrow(()
                -> new RestApiException(MemberError.NULL_MEMBER));
        member.setPassword(passwordEncoder.encrypt(resetPasswordRequest.newPassword()));
        return MemberResponse.PASSWORD_CHANGE_SUCCESS.getMessage();
    }

    private Member getMember(final LoginMember loginMember) {
        return memberRepository.findById(loginMember.memberId()).orElseThrow(()
                -> new RestApiException(MemberError.NULL_MEMBER));
    }

    private Token getToken(final Member member) {
        return tokenRepository.findByMember(member).orElseThrow(()
                -> new RestApiException(TokenError.NULL_REFRESH_TOKEN));
    }
}