package com.example.rightbackend.chat.domain.repository;

import com.example.rightbackend.chat.domain.Message;
import com.example.rightbackend.chat.domain.model.ChatMessageSummary;

import java.util.List;

public interface CustomMessageRepository {
    void markMessagesAsRead(Long chatRoomId, Long readBy);
    List<Message> findMessages(Long chatRoomId, String lastMessageId);
    List<Message> findMessages(Long chatRoomId, String lastMessageId, int limit);
    List<ChatMessageSummary> aggregateMessageSummaries(List<Long> chatRoomIds, Long memberId);
}