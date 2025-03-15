package com.example.rightbackend.member.controller.dto.response;

import java.util.List;

public record MemberPageResponse (String name,
                                  String nickname,
                                  String address,
                                  String height,
                                  String body_type,
                                  String job,
                                  List<String> interests,
                                  String myself){
}