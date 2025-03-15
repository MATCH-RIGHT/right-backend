package com.example.rightbackend.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component @Getter @Setter
public class JWTProperties {
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.accessTokenValidTime}")
    private Long accessTokenValidTime;

    @Value("${jwt.refreshTokenValidTime}")
    private Long refreshTokenValidTime;
}