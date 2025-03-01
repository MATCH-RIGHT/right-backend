package com.example.rightbackend.global.properties;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component @Getter
public class TextEncoderProperties {

    @Value("${encode.secretKey}")
    private String secretKey;

    @Value("${encode.cipher}")
    private String cipher;

    private SecretKeySpec secretKeySpec;

    @PostConstruct
    public void init() {
        byte[] byteKey = secretKey.getBytes(StandardCharsets.UTF_8);
        secretKeySpec = new SecretKeySpec(byteKey, "AES");
    }
}