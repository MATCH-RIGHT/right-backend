package com.example.rightbackend.member.controller.dto.request;

public record ResetPasswordRequest(String name, String phoneNumber, String newPassword) {
}