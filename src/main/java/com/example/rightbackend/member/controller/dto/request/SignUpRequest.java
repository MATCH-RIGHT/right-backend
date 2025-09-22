package com.example.rightbackend.member.controller.dto.request;

import java.util.List;

public record SignUpRequest (String name,
                             String providerId,
                             String password,
                             String phoneNumber,
                             String nickname,
                             String gender,
                             String birthday,
                             Integer location,
                             Integer height,
                             Integer bodyType,
                             Integer job,
                             List<Long> interests,
                             String introduction){
}