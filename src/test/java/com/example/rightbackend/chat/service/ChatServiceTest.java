package com.example.rightbackend.chat.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.MemberRole;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.chat.controller.dto.request.MessageSendRequest;
import com.example.rightbackend.chat.controller.dto.request.ReadRequest;
import com.example.rightbackend.chat.controller.dto.response.ChatSummaryResponse;
import com.example.rightbackend.chat.domain.ChatRoom;
import com.example.rightbackend.chat.domain.Message;
import com.example.rightbackend.chat.domain.model.MemberPair;
import com.example.rightbackend.chat.domain.repository.ChatRoomRepository;
import com.example.rightbackend.chat.domain.repository.MessageRepository;
import com.example.rightbackend.global.BaseIntegrationTest;
import com.example.rightbackend.global.DummyGenerator;
import com.example.rightbackend.global.response.success.ChatSuccess;
import com.example.rightbackend.noti.service.FcmSender;
import com.example.rightbackend.noti.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ChatServiceTest extends BaseIntegrationTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DummyGenerator dummyGenerator;

    @MockBean
    private MessageSender messageSender;

    @MockBean
    private FcmSender fcmSender;

    @MockBean
    private NotificationService notificationService;

    private Member sender;
    private Member receiver;
    private ChatRoom chatRoom;

    @BeforeEach
    void setUpTest() {
        // 각 테스트마다 DB 정리
        messageRepository.deleteAll();
        chatRoomRepository.deleteAll();

        sender = dummyGenerator.generateSingleMember();
        receiver = dummyGenerator.generateSingleMember();

        chatRoom = ChatRoom.createOpenChatroom(new MemberPair(sender, receiver));
        chatRoom = chatRoomRepository.save(chatRoom);

        doNothing().when(messageSender).send(any(Message.class));
        doNothing().when(fcmSender).sendChatMessage(any(Message.class));
        doNothing().when(messageSender).sendReadResponse(any(ReadRequest.class));
        doNothing().when(notificationService).saveChatMessageNotification(any(Member.class), any(Long.class), any(String.class), any(String.class));
    }

    @Test
    @DisplayName("메시지 전송")
    void sendMessage() {
        // Given
        MessageSendRequest request = createMessageSendRequest();

        // When
        String result = chatService.sendMessage(request);

        // Then
        verify(messageSender, times(1)).send(any(Message.class));
        verify(fcmSender, times(1)).sendChatMessage(any(Message.class));
        verify(notificationService, times(1)).saveChatMessageNotification(any(Member.class), any(Long.class), any(String.class), any(String.class));
        assertEquals(ChatSuccess.SEND_MESSAGE_SUCCESS.getMessage(), result);

        List<Message> savedMessages = messageRepository.findAll();
        assertEquals(1, savedMessages.size());
        assertEquals(request.content(), savedMessages.get(0).getContent());
    }

    @Test
    @DisplayName("메시지 읽음 처리")
    void readMessages() {
        // Given
        saveMessage();
        ReadRequest request = new ReadRequest(chatRoom.getId(), receiver.getId());

        // When
        String result = chatService.readMessages(request);

        // Then
        verify(messageSender, times(1)).sendReadResponse(request);
        assertEquals(ChatSuccess.READ_MESSAGES_SUCCESS.getMessage(), result);
    }

    @Test
    @DisplayName("채팅 요약 정보 조회")
    void getChatSummaries() {
        // Given
        saveMessage();
        LoginMember loginMember = new LoginMember(receiver.getId(), MemberRole.MEMBER);

        // When
        ChatSummaryResponse result = chatService.getChatSummaries(loginMember);

        // Then
        assertNotNull(result);
        assertEquals(1, result.chatRooms().size());
    }

    @Test
    @DisplayName("채팅 요약 정보 무한 스크롤 조회")
    void getChatSummariesWithPaging() {
        // Given
        saveMessage();
        LoginMember loginMember = new LoginMember(receiver.getId(), MemberRole.MEMBER);

        // When
        ChatSummaryResponse result = chatService.getChatSummaries(loginMember, null, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.chatRooms().size());
    }

    @Test
    @DisplayName("메시지 목록 조회")
    void getMessages() {
        // Given
        saveMessage();

        // When
        List<Message> result = chatService.getMessages(chatRoom.getId(), null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("메시지 목록 무한 스크롤 조회")
    void getMessagesWithPaging() {
        // Given
        saveMessage();
        saveMessage();

        // When
        List<Message> result = chatService.getMessages(chatRoom.getId(), null, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("채팅방 목록 조회")
    void getChatRooms() {
        // Given
        saveMessage();

        // When
        List<ChatRoom> result = chatService.getChatRooms(receiver.getId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("채팅방 목록 무한 스크롤 조회")
    void getChatRoomsWithPaging() {
        // Given
        saveMessage();

        // When
        List<ChatRoom> result = chatService.getChatRooms(receiver.getId(), null, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    private void saveMessage() {
        MessageSendRequest request = createMessageSendRequest();
        chatService.sendMessage(request);
    }

    private MessageSendRequest createMessageSendRequest() {
        return new MessageSendRequest("안녕하세요", chatRoom.getId(), sender.getId(), receiver.getId());
    }
}
