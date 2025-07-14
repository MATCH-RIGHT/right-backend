package com.example.rightbackend.chat.domain;

import com.example.rightbackend.auth.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ChatParticipantTest {

    @Test
    @DisplayName("채팅 참여자 생성")
    void createChatParticipant() {
        // Given
        Member member = mock(Member.class);
        Member partner = mock(Member.class);

        // When
        ChatRoom chatRoom = mock(ChatRoom.class);
        ChatParticipant participant = ChatParticipant.of(chatRoom, member, partner);

        // Then
        assertEquals(member, participant.getMember());
        assertEquals(chatRoom, participant.getChatRoom());
    }
}
