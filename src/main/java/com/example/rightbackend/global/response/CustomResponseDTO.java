package com.example.rightbackend.global.response;

public record CustomResponseDTO<T>(String code, T result) {
}