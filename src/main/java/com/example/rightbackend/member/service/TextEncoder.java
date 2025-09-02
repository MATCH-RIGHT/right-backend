package com.example.rightbackend.member.service;

import com.example.rightbackend.global.properties.TextEncoderProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TextEncoder {

    private static TextEncoderProperties textEncoderProperties;

    private TextEncoder(TextEncoderProperties textEncoderProperties) {
        TextEncoder.textEncoderProperties = textEncoderProperties;
    }

    public static String encrypt(String text) {
        try {
            Cipher cipher = Cipher.getInstance(textEncoderProperties.getCipher());
            cipher.init(Cipher.ENCRYPT_MODE, textEncoderProperties.getSecretKeySpec());
            byte[] encryptBytes = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptBytes);
        } catch (Exception e) {
            throw new RuntimeException(e); // 상세 오류 기재 필요
        }
    }

    public static String decrypt(String text) {
        try {
            if (text == null || text.isEmpty()) {
                return "";
            }
            
            String base64Text = text.replace('-', '+').replace('_', '/');
            
            int padding = 4 - base64Text.length() % 4;
            if (padding != 4) {
                base64Text += "=".repeat(padding);
            }
            
            Cipher cipher = Cipher.getInstance(textEncoderProperties.getCipher());
            cipher.init(Cipher.DECRYPT_MODE, textEncoderProperties.getSecretKeySpec());
            byte[] decodeBytes = Base64.getDecoder().decode(base64Text);
            byte[] decryptBytes = cipher.doFinal(decodeBytes);
            return new String(decryptBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return text != null ? text : "";
        } catch (Exception e) {
            throw new RuntimeException("복호화 실패: " + e.getMessage(), e);
        }
    }
}