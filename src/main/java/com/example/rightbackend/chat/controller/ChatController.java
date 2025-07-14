package com.example.rightbackend.chat.controller;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.chat.controller.dto.request.MessageSendRequest;
import com.example.rightbackend.chat.controller.dto.request.ReadRequest;
import com.example.rightbackend.chat.controller.dto.response.ChatSummaryResponse;
import com.example.rightbackend.chat.domain.Message;
import com.example.rightbackend.chat.service.ChatService;
import com.example.rightbackend.global.config.resolver.Login;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.error.ChatError;
import com.example.rightbackend.global.response.success.ChatSuccess;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat/send")
    public void sendMessage(@Valid @Payload MessageSendRequest messageSendRequest) {
        try {
            log.debug("Received message send request: roomId={}, senderId={}", 
                    messageSendRequest.roomId(), messageSendRequest.senderId());
            chatService.sendMessage(messageSendRequest);
        } catch (Exception e) {
            log.error("Failed to handle message send request: {}", e.getMessage(), e);
            // WebSocket에서는 직접적인 에러 응답이 어려우므로 로깅만 수행
        }
    }

    @MessageMapping("/chat/read")
    public void readMessages(@Valid @Payload ReadRequest readRequest) {
        try {
            log.debug("Received message read request: roomId={}, memberId={}", 
                    readRequest.roomId(), readRequest.memberId());
            chatService.readMessages(readRequest);
        } catch (Exception e) {
            log.error("Failed to handle message read request: {}", e.getMessage(), e);
            // WebSocket에서는 직접적인 에러 응답이 어려우므로 로깅만 수행
        }
    }

    @GetMapping("/chat/summaries")
    public ResponseEntity<SuccessResponse<ChatSummaryResponse>> getSummaries(
            @Login LoginMember loginMember,
            @RequestParam(value = "lastChatRoomId", required = false) Long lastChatRoomId,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit) {
        
        if (limit <= 0 || limit > 100) {
            limit = 20; // 기본값으로 설정
        }
        
        return SuccessResponse.of(ChatSuccess.GET_SUMMARIES_SUCCESS, 
                chatService.getChatSummaries(loginMember, lastChatRoomId, limit));
    }

    @GetMapping("/chat/room/{roomId}/messages")
    public ResponseEntity<SuccessResponse<List<Message>>> getMessage(
            @PathVariable("roomId") Long chatRoomId, 
            @RequestParam(value = "lastMessageId", required = false) String messageId,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit) {
        
        if (chatRoomId == null || chatRoomId <= 0) {
            throw new RestApiException(ChatError.INVALID_ROOM_ID);
        }
        
        if (limit <= 0 || limit > 100) {
            limit = 20; // 기본값으로 설정
        }
        
        return SuccessResponse.of(ChatSuccess.GET_MESSAGES_SUCCESS, 
                chatService.getMessages(chatRoomId, messageId, limit));
    }
}