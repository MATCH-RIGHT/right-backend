package com.example.rightbackend.member.controller.dto;

import java.util.List;

public record EncodeMemberProfile(String nickname,
                                  String gender,
                                  String birthday,
                                  Integer height,
                                  String bodyType,
                                  String job,
                                  List<Long> interests,
                                  String introduction,
                                  String money) {
}