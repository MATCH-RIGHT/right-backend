package com.example.rightbackend.member.controller.dto.request;

import java.util.List;

public record SignUpRequest (String name,
                             String providerId,
                             String password,
                             String phoneNumber,
                             String nickname,
                             String gender,
                             String birthday,
                             String locationName,
                             String height,
                             String body_type,
                             String job,
                             List<String> interests,
                             String myself){
}