package com.example.rightbackend.member.controller.dto.response;

import java.util.List;

public record MemberPageResponse (String name,
                                  String nickname,
                                  String address,
                                  String height,
                                  String body_type,
                                  String job,
                                  List<InterestDto> interests,
                                  String myself){
    
    public record InterestDto(Long id, String name) {}
}