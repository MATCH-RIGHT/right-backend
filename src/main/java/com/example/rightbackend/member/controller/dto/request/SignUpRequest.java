package com.example.rightbackend.member.controller.dto.request;

public record SignUpRequest (
                             String provider,
                             String providerId,
                             String password,
                             String phoneNumber,
                             String nickname,
                             String gender,
                             String birthday,
                             String address,
                             String height,
                             String body_type,
                             String job,
                             String myself){
}