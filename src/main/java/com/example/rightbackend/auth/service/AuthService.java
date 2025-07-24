package com.example.rightbackend.auth.service;

import com.example.rightbackend.auth.controller.dto.AuthToken;
import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.controller.dto.LoginRequest;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.Token;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.auth.domain.repository.TokenRepository;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.global.response.error.TokenError;
import com.example.rightbackend.member.controller.dto.response.MemberResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(final MemberRepository memberRepository, final TokenRepository tokenRepository, final TokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthToken login(LoginRequest loginRequest) {
        validateLoginRequest(loginRequest);
        Member member = getMemberEntityFromId(loginRequest.id());
        passwordCheck(loginRequest.password(), member);
        return generateAuthToken(member);
    }

    private void validateLoginRequest(final LoginRequest loginRequest) {
        if (loginRequest.id() == null || loginRequest.id().trim().isEmpty()) {
            throw new RestApiException(MemberError.INVALID_ID);
        }
        if (loginRequest.password() == null || loginRequest.password().trim().isEmpty()) {
            throw new RestApiException(MemberError.EMPTY_PASSWORD);
        }
    }

    private Member getMemberEntityFromId(final String id) {
        Member member = memberRepository.findByProviderId(id).orElseThrow(()
                -> new RestApiException(MemberError.NULL_MEMBER));
        if(member.getWithdraw()){
            throw new RestApiException(MemberError.WITHDRAW_MEMBER);
        }
        return member;
    }

    private void passwordCheck(final String password, final Member member) {
        if(!member.getPassword().equals(passwordEncoder.encrypt(password))) {
            throw new RestApiException(MemberError.INVALID_PASSWORD);
        }
    }

    private AuthToken generateAuthToken(final Member member) {
        LoginMember loginMember = member.getLoginMember();

        String accessToken = tokenProvider.generateAccessToken(loginMember);
        String refreshToken = generateRefreshToken(member, loginMember);

        return new AuthToken(accessToken, refreshToken);
    }

    private String generateRefreshToken(final Member member, final LoginMember loginMember) {
        Token token = new Token(member);

        if(tokenRepository.findByMember(member).isPresent()) {
            tokenRepository.deleteByMember(member);
        }

        tokenRepository.save(token);
        String refreshToken = tokenProvider.generateRefreshToken(loginMember, token.getId());

        token.setRefreshToken(refreshToken);
        return refreshToken;
    }

    @Transactional
    public String logout(final AuthToken authToken) {
        Long tokenId = tokenProvider.getTokenIdFromRefreshToken(authToken.refreshToken());
        Token token = getTokenEntity(tokenId);
        token.setExpired(true);
        return MemberResponse.LOGOUT_SUCCESS.getMessage();
    }

    private Token getTokenEntity(final Long tokenId) {
        return tokenRepository.findById(tokenId).orElseThrow(()
            -> new RestApiException(TokenError.NULL_REFRESH_TOKEN));
    }

    @Transactional
    public AuthToken reissue(final AuthToken authToken) {
        validateAuthToken(authToken);
        tokenExipreCheck(authToken);
        LoginMember loginMember = tokenProvider.getLoginFromToken(authToken.refreshToken());
        String newAccessToken = tokenProvider.generateAccessToken(loginMember);
        return new AuthToken(newAccessToken, authToken.refreshToken());
    }

    private void validateAuthToken(final AuthToken authToken) {
        if (authToken.refreshToken() == null || authToken.refreshToken().trim().isEmpty()) {
            throw new RestApiException(TokenError.NULL_REFRESH_TOKEN);
        }
    }

    private void tokenExipreCheck(final AuthToken authToken) {
        if(tokenProvider.isNotExpiredToken(authToken.accessToken())) {
            throw new RestApiException(TokenError.NOT_ACCESS_TOKEN_FOR_REISSUE);
        }
        if(!tokenProvider.isNotExpiredToken(authToken.refreshToken())) {
            throw new RestApiException(TokenError.EXPIRED_REFRESH_TOKEN);
        }
        dbTokenExpiredCheck(authToken.refreshToken());
    }

    private void dbTokenExpiredCheck(final String refreshToken) {
        Long tokenId = tokenProvider.getTokenIdFromRefreshToken(refreshToken);
        Token token = getTokenEntity(tokenId);
        if(token.isExpired()) {
            throw new RestApiException(TokenError.EXPIRED_REFRESH_TOKEN);
        }
        token.changeRecentLogin();
    }

    @Transactional
    public String withDraw(final LoginMember loginMember) {
        Member member = getMember(loginMember);
        member.setWithdraw(true);
        Token token = getToken(member);
        token.setExpired(true);
        return MemberResponse.WITHDRAW_SUCCESS.getMessage();
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