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
import com.example.rightbackend.member.controller.dto.request.*;
import com.example.rightbackend.member.controller.dto.response.MemberPageResponse;
import com.example.rightbackend.member.controller.dto.response.MemberResponse;
import com.example.rightbackend.member.domain.Interest;
import com.example.rightbackend.member.domain.Location;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.domain.MemberProfileToInterest;
import com.example.rightbackend.member.domain.repository.InterestRepository;
import com.example.rightbackend.member.domain.repository.LocationRepository;
import com.example.rightbackend.member.domain.repository.MemberProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberProfileService {

    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final InterestRepository interestRepository;
    private final LocationRepository locationRepository;

    public MemberProfileService(PasswordEncoder passwordEncoder, TokenRepository tokenRepository, MemberRepository memberRepository, 
                             final MemberProfileRepository memberProfileRepository, InterestRepository interestRepository,
                             LocationRepository locationRepository) {
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.memberRepository = memberRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.interestRepository = interestRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public String signUp(final SignUpRequest request) {
        validateSignUpRequest(request);

        Member member = saveMemberEntity(request);

        MemberProfile memberProfile = getMemberProfileEntity(request, member);

        addInterestsToMemberProfile(memberProfile, request.interests());
        
        if (request.locationName() != null && !request.locationName().isEmpty()) {
            Location location = locationRepository.findByName(request.locationName())
                .orElseGet(() -> locationRepository.save(Location.of(request.locationName())));
            memberProfile.addLocation(location);
        }

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
                TextEncoder.encrypt(request.height()),
                TextEncoder.encrypt(request.body_type()),
                TextEncoder.encrypt(request.job()),
                request.interests(),
                request.myself(),
                TextEncoder.encrypt(request.gender()));
    }

    @Transactional(readOnly = true)
    public String checkDuplicateId(final CheckIdRequest request) {
        validateProviderId(request.providerId());
        if(memberRepository.findByProviderId(request.providerId()).isPresent()) {
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
        validateResetPasswordRequest(resetPasswordRequest);
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

    @Transactional(readOnly = true)
    public List<Interest> getAllInterests() {
        return interestRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }
    
    @Transactional
    public String updateProfile(final LoginMember loginMember, final UpdateProfileRequest request) {
        Member member = getMember(loginMember);
        MemberProfile memberProfile = member.getMemberProfile();
        
        if (request.nickname() != null) memberProfile.setNickname(TextEncoder.encrypt(request.nickname()));
        if (request.gender() != null) memberProfile.setGender(TextEncoder.encrypt(request.gender()));
        if (request.birthday() != null) memberProfile.setBirthday(TextEncoder.encrypt(request.birthday()));
        if (request.height() != null) memberProfile.setHeight(TextEncoder.encrypt(request.height()));
        if (request.body_type() != null) memberProfile.setBody_type(TextEncoder.encrypt(request.body_type()));
        if (request.job() != null) memberProfile.setJob(TextEncoder.encrypt(request.job()));
        if (request.myself() != null) memberProfile.setMyself(request.myself());
        
        if (request.interests() != null) {
            memberProfile.getMemberProfileToInterests().clear();
            addInterestsToMemberProfile(memberProfile, request.interests());
        }
        
        if (request.locationName() != null) {
            memberProfile.getMemberProfileToLocations().clear();
            Location location = locationRepository.findByName(request.locationName())
                .orElseGet(() -> locationRepository.save(Location.of(request.locationName())));
            memberProfile.addLocation(location);
        }
        
        memberProfileRepository.save(memberProfile);
        
        return MemberResponse.PROFILE_UPDATE_SUCCESS.getMessage();
    }

    private void validateSignUpRequest(final SignUpRequest request) {
        validateName(request.name());
        validateProviderId(request.providerId());
        validatePassword(request.password());
        validatePhoneNumber(request.phoneNumber());
        validateNickname(request.nickname());
        if (request.height() != null && !request.height().trim().isEmpty()) {
            validateHeight(request.height());
        }
        if (request.birthday() != null && !request.birthday().trim().isEmpty()) {
            validateBirthday(request.birthday());
        }
    }

    private void validateResetPasswordRequest(final ResetPasswordRequest request) {
        validateName(request.name());
        validatePhoneNumber(request.phoneNumber());
        validatePassword(request.newPassword());
    }

    private void validateName(final String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RestApiException(MemberError.INVALID_NAME_FORMAT);
        }
    }

    private void validateProviderId(final String providerId) {
        if (providerId == null || providerId.trim().isEmpty()) {
            throw new RestApiException(MemberError.INVALID_ID);
        }
        if (!providerId.matches("^[a-zA-Z0-9]{3,20}$")) {
            throw new RestApiException(MemberError.INVALID_PROVIDER_ID_FORMAT);
        }
    }

       private void validatePassword(final String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new RestApiException(MemberError.EMPTY_PASSWORD);
        }
        if (!(password.length() >= 8 && password.length() <= 24) || 
            !password.matches(".*[a-zA-Z].*") || 
            !password.matches(".*[0-9].*") || 
            !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new RestApiException(MemberError.WEAK_PASSWORD);
        }
    }

    private void validatePhoneNumber(final String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new RestApiException(MemberError.INVALID_PHONE_FORMAT);
        }
        if (!phoneNumber.matches("^010-?\\d{4}-?\\d{4}$")) {
            throw new RestApiException(MemberError.INVALID_PHONE_FORMAT);
        }
    }

    private void validateNickname(final String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new RestApiException(MemberError.INVALID_NICKNAME_FORMAT);
        }
    }

    private void validateHeight(final String height) {
        try {
            int heightValue = Integer.parseInt(height);
            if (heightValue < 120 || heightValue > 200) {
                throw new RestApiException(MemberError.INVALID_HEIGHT_FORMAT);
            }
        } catch (NumberFormatException e) {
            throw new RestApiException(MemberError.INVALID_HEIGHT_FORMAT);
        }
    }

    private void validateBirthday(final String birthday) {
        if (!birthday.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new RestApiException(MemberError.INVALID_BIRTHDAY_FORMAT);
        }
    }
}