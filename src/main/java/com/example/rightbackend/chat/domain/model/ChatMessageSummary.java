package com.example.rightbackend.chat.domain.model;

import java.util.Date;

public record ChatMessageSummary(Long roomId, String lastMessageContent, Date lastMessageTime, Long numberOfUnReadMessages) {
}