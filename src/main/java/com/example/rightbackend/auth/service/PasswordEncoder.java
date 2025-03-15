package com.example.rightbackend.auth.service;

import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.properties.PasswordEncoderProperties;
import com.example.rightbackend.global.response.error.MemberError;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Component
public class  PasswordEncoder {

    private final PasswordEncoderProperties passwordEncoderProperties;

    public PasswordEncoder(final PasswordEncoderProperties passwordEncoderProperties) {
        this.passwordEncoderProperties = passwordEncoderProperties;
    }

    public String encrypt(String password) {
        try {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), passwordEncoderProperties.getSecretKey(), 65536, 128);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] hash = secretKeyFactory.generateSecret(keySpec).getEncoded();

            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RestApiException(MemberError.INVALID_PASSWORD);
        }
    }
}