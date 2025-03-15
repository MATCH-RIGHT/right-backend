package com.example.rightbackend.global.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PasswordEncoderProperties {

    @Value("${passwordEncode.key}")
    private String secretKey;

    public byte[] getSecretKey() {
        return secretKey.getBytes(StandardCharsets.UTF_8);
    }
}