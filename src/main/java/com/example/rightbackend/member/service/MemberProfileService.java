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
import com.example.rightbackend.member.domain.constant.Gender;
import com.example.rightbackend.member.domain.constant.BodyType;
import com.example.rightbackend.member.domain.constant.Job;
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
        
        if (request.location() != null) {
            Location location = locationRepository.findById(request.location().longValue())
                .orElseThrow(() -> new RestApiException(MemberError.INVALID_LOCATION_ID));
            memberProfile.addLocation(location);
        }

        memberProfile = memberProfileRepository.save(memberProfile);

        member.setMemberProfile(memberProfile);

        return MemberResponse.SIGN_UP_SUCCESS.getMessage();
    }

    private void addInterestsToMemberProfile(MemberProfile memberProfile, List<Long> interestIds) {
        if(interestIds == null || interestIds.isEmpty()) {
            return;
        }
        for(Long interestId: interestIds) {
            Interest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new RestApiException(MemberError.INVALID_INTEREST_ID));
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
                request.providerId(),
                passwordEncoder.encrypt(request.password()),
                TextEncoder.encrypt(request.phoneNumber()));
    }

    private MemberProfile getMemberProfileEntity(final SignUpRequest request, final Member member) {
        EncodeMemberProfile encodeProfile = encodeForMemberProfile(request);
        return MemberProfile.of(encodeProfile, member);
    }

    private EncodeMemberProfile encodeForMemberProfile(final SignUpRequest request) {
        String genderStr = request.gender() != null ? Gender.fromName(request.gender()).name() : null;
        String bodyTypeStr = request.bodyType() != null ? BodyType.fromId(request.bodyType()).name() : null;
        String jobStr = request.job() != null ? Job.fromId(request.job()).name() : null;

        return new EncodeMemberProfile(TextEncoder.encrypt(request.nickname()),
                genderStr != null ? TextEncoder.encrypt(genderStr) : null,
                TextEncoder.encrypt(request.birthday()),
                request.height(),
                bodyTypeStr != null ? TextEncoder.encrypt(bodyTypeStr) : null,
                jobStr != null ? TextEncoder.encrypt(jobStr) : null,
                request.interests(),
                TextEncoder.encrypt(request.introduction()),
                "0");
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
        String encryptedPhoneNumber = TextEncoder.encrypt(searchIdRequest.phoneNumber());
        Member member = memberRepository.findFirstByNameAndPhoneNumber(searchIdRequest.name(), encryptedPhoneNumber).orElseThrow(()
                -> new RestApiException(MemberError.NULL_MEMBER));
        return member.getProviderId();
    }

    @Transactional
    public String resetPassword(final ResetPasswordRequest resetPasswordRequest) {
        validateResetPasswordRequest(resetPasswordRequest);
        String encryptedPhoneNumber = TextEncoder.encrypt(resetPasswordRequest.phoneNumber());
        Member member = memberRepository.findFirstByNameAndPhoneNumber(resetPasswordRequest.name(), encryptedPhoneNumber).orElseThrow(()
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
        if (request.gender() != null) {
            String genderStr = Gender.fromName(request.gender()).name();
            memberProfile.setGender(TextEncoder.encrypt(genderStr));
        }
        if (request.birthday() != null) memberProfile.setBirthday(TextEncoder.encrypt(request.birthday()));
        if (request.height() != null) memberProfile.setHeight(request.height());
        if (request.bodyType() != null) {
            String bodyTypeStr = BodyType.fromId(request.bodyType()).name();
            memberProfile.setBodyType(TextEncoder.encrypt(bodyTypeStr));
        }
        if (request.job() != null) {
            String jobStr = Job.fromId(request.job()).name();
            memberProfile.setJob(TextEncoder.encrypt(jobStr));
        }
        if (request.introduction() != null) memberProfile.setIntroduction(TextEncoder.encrypt(request.introduction()));
        
        if (request.interests() != null) {
            memberProfile.getMemberProfileToInterests().clear();
            addInterestsToMemberProfile(memberProfile, request.interests());
        }
        
        if (request.location() != null) {
            memberProfile.getMemberProfileToLocations().clear();
            Location location = locationRepository.findById(request.location().longValue())
                .orElseThrow(() -> new RestApiException(MemberError.INVALID_LOCATION_ID));
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
        validateGender(request.gender());
        validateBodyType(request.bodyType());
        validateJob(request.job());
        if (request.height() != null) {
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

    private void validateHeight(final Integer height) {
        if (height < 120 || height > 250) {
            throw new RestApiException(MemberError.INVALID_HEIGHT_FORMAT);
        }
    }

    private void validateBirthday(final String birthday) {
        if (!birthday.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new RestApiException(MemberError.INVALID_BIRTHDAY_FORMAT);
        }
    }

    private void validateGender(final String gender) {
        if (gender != null && !gender.trim().isEmpty()) {
            try {
                Gender.fromName(gender);
            } catch (IllegalArgumentException e) {
                throw new RestApiException(MemberError.INVALID_GENDER);
            }
        }
    }

    private void validateBodyType(final Integer bodyType) {
        if (bodyType != null) {
            try {
                BodyType.fromId(bodyType);
            } catch (IllegalArgumentException e) {
                throw new RestApiException(MemberError.INVALID_BODY_TYPE);
            }
        }
    }

    private void validateJob(final Integer job) {
        if (job != null) {
            try {
                Job.fromId(job);
            } catch (IllegalArgumentException e) {
                throw new RestApiException(MemberError.INVALID_JOB);
            }
        }
    }
}