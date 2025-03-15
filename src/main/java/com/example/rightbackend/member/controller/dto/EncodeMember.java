package com.example.rightbackend.member.controller.dto;

public record EncodeMember(String name,
                           String provider,
                           String providerId,
                           String password,
                           String phoneNumber){
}