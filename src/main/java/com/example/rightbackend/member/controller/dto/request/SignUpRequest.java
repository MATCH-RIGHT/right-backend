package com.example.rightbackend.member.controller.dto.request;

import java.util.List;

public record SignUpRequest (String name,
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
                             List<String> interests,
                             String myself){
}