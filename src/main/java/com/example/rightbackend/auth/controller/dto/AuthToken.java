package com.example.rightbackend.auth.controller.dto;

public record AuthToken (String accessToken, String refreshToken) {
}