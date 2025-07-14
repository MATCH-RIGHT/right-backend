package com.example.rightbackend.chat.controller.dto.request;

import jakarta.validation.constraints.NotNull;

public record ReadRequest(
    @NotNull(message = "채팅방 ID는 필수입니다")
    Long roomId,
    
    @NotNull(message = "회원 ID는 필수입니다")
    Long memberId
) {
}