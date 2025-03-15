package com.example.rightbackend.auth.controller.dto;

import com.example.rightbackend.auth.domain.MemberRole;

public record LoginMember(Long memberId, MemberRole role) {
}