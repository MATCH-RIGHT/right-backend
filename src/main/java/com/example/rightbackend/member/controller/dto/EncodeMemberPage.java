package com.example.rightbackend.member.controller.dto;

import java.util.List;

public record EncodeMemberPage (String nickname,
                                String address,
                                String height,
                                String body_type,
                                String job,
                                List<String> interests,
                                String myself){
}