package com.example.rightbackend.chat.domain;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.chat.domain.model.MemberPair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ChatRoomTest {

    @Test
    @DisplayName("채팅방 생성")
    void createChatRoom() {
        // Given
        Member member1 = mock(Member.class);
        Member member2 = mock(Member.class);

        // When
        ChatRoom chatRoom = ChatRoom.createOpenChatroom(new MemberPair(member1, member2));

        // Then
        assertEquals(ChatRoomStatus.OPEN, chatRoom.getStatus());
        assertEquals(2, chatRoom.getParticipants().size());
        assertTrue(chatRoom.getParticipants().stream().anyMatch(p -> p.getMember() == member1));
        assertTrue(chatRoom.getParticipants().stream().anyMatch(p -> p.getMember() == member2));
    }

    @Test
    @DisplayName("채팅방이 특정 회원을 포함하는지 확인")
    void containsMember() {
        // Given
        Member member1 = mock(Member.class);
        Member member2 = mock(Member.class);
        Member other = mock(Member.class);

        ChatRoom chatRoom = ChatRoom.createOpenChatroom(new MemberPair(member1, member2));

        // When & Then
        assertTrue(chatRoom.getParticipants().stream().anyMatch(p -> p.getMember() == member1));
        assertTrue(chatRoom.getParticipants().stream().anyMatch(p -> p.getMember() == member2));
        assertFalse(chatRoom.getParticipants().stream().anyMatch(p -> p.getMember() == other));
    }
}
