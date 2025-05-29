package com.example.rightbackend.member.controller.dto.request;

import java.util.List;

public record UpdateProfileRequest(
    String nickname,
    String gender,
    String birthday,
    String locationName,
    String height,
    String body_type,
    String job,
    List<String> interests,
    String myself
) {
}
