package com.example.rightbackend.chat.controller.dto.response;

import com.example.rightbackend.chat.domain.ChatRoom;
import com.example.rightbackend.chat.domain.model.ChatMessageSummary;

import java.util.List;

public record ChatSummaryResponse(List<ChatRoom> chatRooms, List<ChatMessageSummary> chatMessageSummaries) {
}