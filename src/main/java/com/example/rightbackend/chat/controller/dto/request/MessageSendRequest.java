package com.example.rightbackend.chat.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MessageSendRequest(
    @NotBlank(message = "메시지 내용은 필수입니다")
    @Size(max = 1000, message = "메시지는 1000자를 초과할 수 없습니다")
    String content,
    
    @NotNull(message = "채팅방 ID는 필수입니다")
    Long roomId,
    
    @NotNull(message = "발신자 ID는 필수입니다")
    Long senderId,
    
    @NotNull(message = "수신자 ID는 필수입니다")
    Long receiverId
) {
}