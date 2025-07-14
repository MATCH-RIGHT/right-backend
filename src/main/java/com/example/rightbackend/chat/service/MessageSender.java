package com.example.rightbackend.chat.service;

import com.example.rightbackend.chat.controller.dto.request.ReadRequest;
import com.example.rightbackend.chat.controller.dto.response.ReadResponse;
import com.example.rightbackend.chat.domain.Message;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.ChatError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageSender {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public MessageSender(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void send(Message message) {
        try {
            if (message == null || message.getRoomId() == null) {
                throw new RestApiException(ChatError.INVALID_ROOM_ID);
            }
            
            String destination = "/room/" + message.getRoomId();
            simpMessagingTemplate.convertAndSend(destination, message);
            log.debug("Message sent via WebSocket: roomId={}, messageId={}", 
                    message.getRoomId(), message.getId());
            
        } catch (Exception e) {
            log.error("Failed to send WebSocket message: roomId={}, messageId={}", 
                    message.getRoomId(), message.getId(), e);
            throw new RestApiException(ChatError.WEBSOCKET_CONNECTION_ERROR);
        }
    }

    public void sendReadResponse(ReadRequest readRequest) {
        try {
            if (readRequest == null || readRequest.roomId() == null || readRequest.memberId() == null) {
                throw new RestApiException(ChatError.INVALID_ROOM_ID);
            }
            
            ReadResponse readResponse = new ReadResponse(readRequest.memberId());
            String destination = "/room/" + readRequest.roomId();
            simpMessagingTemplate.convertAndSend(destination, readResponse);
            log.debug("Read response sent via WebSocket: roomId={}, memberId={}", 
                    readRequest.roomId(), readRequest.memberId());
            
        } catch (Exception e) {
            log.error("Failed to send WebSocket read response: roomId={}, memberId={}", 
                    readRequest.roomId(), readRequest.memberId(), e);
            throw new RestApiException(ChatError.WEBSOCKET_CONNECTION_ERROR);
        }
    }
}