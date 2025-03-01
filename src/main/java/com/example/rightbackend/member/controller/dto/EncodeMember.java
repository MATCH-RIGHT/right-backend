package com.example.rightbackend.member.controller.dto;

public record EncodeMember(String provider,
                           String provider_id,
                           String password,
                           String phoneNumber){
}