package com.example.rightbackend.member.controller.dto;

import com.example.rightbackend.member.controller.dto.response.MemberPageResponse.InterestDto;
import java.util.List;

public record EncodeMemberPage (String nickname,
                                String locationName,
                                String height,
                                String body_type,
                                String job,
                                List<InterestDto> interests,
                                String myself){
}