package com.example.rightbackend.member.controller.dto;

public record EncodeSignUp(String provider,
                           String provider_id,
                           String password,
                           String phoneNumber,
                           String nickname,
                           String gender,
                           String birthday,
                           String address,
                           String height,
                           String body_type,
                           String job,
                           String money,
                           String myself) {
}