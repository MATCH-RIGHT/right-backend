package com.example.rightbackend.chat.domain;

import com.example.rightbackend.chat.controller.dto.request.MessageSendRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    @DisplayName("MessageSendRequest로부터 Message 객체 생성")
    void createFromRequest() {
        // Given
        Long roomId = 1L;
        Long senderId = 2L;
        Long receiverId = 3L;
        String content = "안녕하세요";
        MessageSendRequest request = new MessageSendRequest(content, roomId, senderId, receiverId);

        // When
        Message message = Message.from(request);

        // Then
        assertEquals(content, message.getContent());
        assertEquals(roomId, message.getRoomId());
        assertEquals(senderId, message.getSenderId());
        assertEquals(receiverId, message.getReceiverId());
        assertFalse(message.getIsRead());
        assertNotNull(message.getSendTime());
    }
}
