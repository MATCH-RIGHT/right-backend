package com.example.rightbackend.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component @Getter @Setter
public class JWTProperties {
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.accessTokenValidTime}")
    private Long accessTokenValidTime;

    @Value("${jwt.refreshTokenValidTime}")
    private Long refreshTokenValidTime;

    public byte[] getBytesSecretKey() {
        return secretKey.getBytes(StandardCharsets.UTF_8);
    }
}