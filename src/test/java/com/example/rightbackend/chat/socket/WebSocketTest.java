package com.example.rightbackend.chat.socket;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.chat.controller.dto.request.MessageSendRequest;
import com.example.rightbackend.chat.controller.dto.request.ReadRequest;
import com.example.rightbackend.chat.controller.dto.response.ReadResponse;
import com.example.rightbackend.chat.domain.ChatRoom;
import com.example.rightbackend.chat.domain.model.MemberPair;
import com.example.rightbackend.chat.domain.repository.ChatRoomRepository;
import com.example.rightbackend.global.BaseIntegrationTest;
import com.example.rightbackend.global.DummyGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DummyGenerator dummyGenerator;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    private String websocketUrl;
    private Member sender;
    private Member receiver;
    private ChatRoom chatRoom;
    private WebSocketStompClient stompClient;
    private StompSession stompSession;

    @BeforeEach
    void setUpTest() {
        websocketUrl = "ws://localhost:" + port + "/ws";
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        sender = dummyGenerator.generateSingleMember();
        receiver = dummyGenerator.generateSingleMember();

        chatRoom = ChatRoom.createOpenChatroom(new MemberPair(sender, receiver));
        chatRoom = chatRoomRepository.save(chatRoom);
    }

    @AfterEach
    void tearDown() {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
    }

    @Test
    @DisplayName("웹소켓 연결")
    void connectWebSocket() throws Exception {
        stompSession = stompClient
                .connectAsync(websocketUrl, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        assertNotNull(stompSession);
        assertTrue(stompSession.isConnected());
    }

    @Test
    @DisplayName("메시지 전송")
    void sendMessage() throws Exception {
        stompSession = stompClient
                .connectAsync(websocketUrl, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        MessageSendRequest request = new MessageSendRequest("안녕하세요", chatRoom.getId(), sender.getId(), receiver.getId());

        CompletableFuture<MessageSendRequest> future = new CompletableFuture<>();

        stompSession.subscribe("/room/" + chatRoom.getId(), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageSendRequest.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                future.complete((MessageSendRequest) payload);
            }
        });

        stompSession.send("/app/chat/send", request);

        MessageSendRequest response = future.get(5, TimeUnit.SECONDS);
        assertEquals("안녕하세요", response.content());
    }

    @Test
    @DisplayName("메시지 읽음 처리")
    void readMessages() throws Exception {
        stompSession = stompClient
                .connectAsync(websocketUrl, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        ReadRequest request = new ReadRequest(chatRoom.getId(), receiver.getId());

        CompletableFuture<ReadResponse> future = new CompletableFuture<>();

        stompSession.subscribe("/room/" + chatRoom.getId(), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ReadResponse.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                future.complete((ReadResponse) payload);
            }
        });

        stompSession.send("/app/chat/read", request);

        ReadResponse response = future.get(5, TimeUnit.SECONDS);
        assertEquals(receiver.getId(), response.readBy());
    }
}