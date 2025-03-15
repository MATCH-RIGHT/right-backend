package com.example.rightbackend.member.controller.dto;

import java.util.List;

public record EncodeMemberProfile(String nickname,
                                  String gender,
                                  String birthday,
                                  String address,
                                  String height,
                                  String body_type,
                                  String job,
                                  List<String> interests,
                                  String money,
                                  String myself) {
}