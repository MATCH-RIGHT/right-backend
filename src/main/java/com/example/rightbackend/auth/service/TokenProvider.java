package com.example.rightbackend.auth.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.MemberRole;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.properties.JWTProperties;
import com.example.rightbackend.global.response.error.TokenError;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenProvider {

    private static final String BEARER_PREFIX = "Bearer ";

    private JWTProperties jwtProperties;

    public TokenProvider(JWTProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    // AccessToken
    public String generateAccessToken(final LoginMember loginMember) {
        Claims claims = getClaimsFrom(loginMember);
        return getTokenFrom(claims, jwtProperties.getAccessTokenValidTime());
    }

    private Claims getClaimsFrom(final LoginMember loginMember) {
        Claims claims = Jwts.claims();
        claims.put("memberId", loginMember.memberId());
        claims.put("role", loginMember.role().getCode());
        return claims;
    }

    private String getTokenFrom(final Claims claims, final Long TokenValidTime) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TokenValidTime))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken
    public String generateRefreshToken(final LoginMember loginMember, final Long tokenId) {
        Claims claims = getClaimsFrom(loginMember, tokenId);
        return getTokenFrom(claims, jwtProperties.getRefreshTokenValidTime());
    }

    private Claims getClaimsFrom(final LoginMember loginMember, final Long tokenId) {
        Claims claims = Jwts.claims();
        claims.put("memberId", loginMember.memberId());
        claims.put("role", loginMember.role().getCode());
        claims.put("tokenId", tokenId);
        return claims;
    }

    public LoginMember getLoginFromToken(final String token) {
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getBytesSecretKey()))
                    .build()
                    .parseClaimsJws(removeTokenPrefix(token))
                    .getBody();
            return new LoginMember(Long.parseLong(String.valueOf(claims.get("memberId"))), MemberRole.of((Integer)claims.get("role")));
        } catch (ExpiredJwtException e){
            throw new RestApiException(TokenError.EXPIRED_ACCESS_TOKEN);
        } catch (Exception e){
            throw new RestApiException(TokenError.INVALID_TOKEN);
        }
    }

    private String removeTokenPrefix(final String token) {
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }
        return token;
    }

    public boolean isNotExpiredToken(final String token) {
        String cleanToken = removeTokenPrefix(token);
        if (cleanToken == null) return false;
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(cleanToken)
                    .getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public Long getTokenIdFromRefreshToken(final String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder().
                    setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getBytesSecretKey())).
                    build().
                    parseClaimsJws(removeTokenPrefix(refreshToken)).
                    getBody();
            return Long.parseLong(String.valueOf(claims.get("tokenId")));
        } catch (ExpiredJwtException e) {
          throw new RestApiException(TokenError.EXPIRED_REFRESH_TOKEN);
        } catch (Exception e) {
            throw new RestApiException(TokenError.INVALID_TOKEN);
        }
    }
}