package com.example.rightbackend.member.controller.dto.response;

import java.util.List;

public record MemberPageResponse (String name,
                                  String nickname,
                                  String address,
                                  Integer height,
                                  String bodyType,
                                  String job,
                                  List<InterestDto> interests,
                                  String introduction){
    
    public record InterestDto(Long id, String name) {}
}