package com.example.rightbackend.chat.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.chat.controller.dto.request.MessageSendRequest;
import com.example.rightbackend.chat.controller.dto.request.ReadRequest;
import com.example.rightbackend.chat.controller.dto.response.ChatSummaryResponse;
import com.example.rightbackend.chat.domain.ChatRoom;
import com.example.rightbackend.chat.domain.ChatRoomStatus;
import com.example.rightbackend.chat.domain.Message;
import com.example.rightbackend.chat.domain.model.ChatMessageSummary;
import com.example.rightbackend.chat.domain.repository.ChatRoomRepository;
import com.example.rightbackend.chat.domain.repository.MessageRepository;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.ChatError;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.global.response.success.ChatSuccess;
import com.example.rightbackend.noti.service.FcmSender;
import com.example.rightbackend.noti.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatService {

    private final MessageSender messageSender;
    private final FcmSender fcmSender;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;

    public ChatService(final MessageSender messageSender, 
                      final FcmSender fcmSender, 
                      final MessageRepository messageRepository, 
                      final ChatRoomRepository chatRoomRepository,
                      final NotificationService notificationService,
                      final MemberRepository memberRepository) {
        this.messageSender = messageSender;
        this.fcmSender = fcmSender;
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.notificationService = notificationService;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public String sendMessage(MessageSendRequest request) {
        try {
            // 입력 검증
            validateMessageRequest(request);
            
            // 채팅방 상태 확인
            ChatRoom chatRoom = validateChatRoom(request.roomId());
            
            // 메시지 저장
            Message savedMessage = saveMessage(request);
            log.info("Message saved: roomId={}, senderId={}, messageId={}", 
                    request.roomId(), request.senderId(), savedMessage.getId());
            
            // WebSocket으로 실시간 전송
            try {
                messageSender.send(savedMessage);
            } catch (Exception e) {
                log.error("Failed to send WebSocket message: {}", e.getMessage());
                // WebSocket 실패는 전체 프로세스를 중단하지 않음
            }
            
            // FCM 푸시 알림 전송 및 알림 저장
            try {
                fcmSender.sendChatMessage(savedMessage);
                
                // 수신자 정보 조회 및 알림 저장
                try {
                    Member receiver = memberRepository.findById(savedMessage.getReceiverId())
                            .orElseThrow(() -> new RestApiException(MemberError.NULL_MEMBER));
                    
                    Member sender = memberRepository.findById(savedMessage.getSenderId())
                            .orElseThrow(() -> new RestApiException(MemberError.NULL_MEMBER));
                    
                    // 채팅 메시지 알림 저장
                    notificationService.saveChatMessageNotification(
                            receiver, 
                            savedMessage.getRoomId(), 
                            sender.getName(), 
                            savedMessage.getContent()
                    );
                    
                    log.debug("Chat message notification saved for receiver: {}, sender: {}", 
                            receiver.getId(), sender.getId());
                            
                } catch (Exception notificationError) {
                    log.error("Failed to save chat message notification: {}", notificationError.getMessage());
                    // 알림 저장 실패는 전체 프로세스를 중단하지 않음
                }
                
            } catch (Exception e) {
                log.error("Failed to send FCM notification: {}", e.getMessage());
                // FCM 실패는 전체 프로세스를 중단하지 않음
            }
            
            return ChatSuccess.SEND_MESSAGE_SUCCESS.getMessage();
            
        } catch (RestApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to send message: roomId={}, senderId={}", 
                    request.roomId(), request.senderId(), e);
            throw new RestApiException(ChatError.MESSAGE_SEND_FAILED);
        }
    }

    private Message saveMessage(MessageSendRequest request) {
        try {
            Message message = Message.from(request);
            return messageRepository.save(message);
        } catch (Exception e) {
            log.error("Failed to save message: {}", e.getMessage());
            throw new RestApiException(ChatError.MESSAGE_SEND_FAILED);
        }
    }

    @Transactional
    public String readMessages(ReadRequest request) {
        try {
            // 입력 검증
            validateReadRequest(request);
            
            // 채팅방 존재 확인
            validateChatRoom(request.roomId());
            
            // 메시지 읽음 처리
            messageRepository.markMessagesAsRead(request.roomId(), request.memberId());
            log.info("Messages marked as read: roomId={}, memberId={}", 
                    request.roomId(), request.memberId());
            
            // WebSocket으로 읽음 상태 전송
            try {
                messageSender.sendReadResponse(request);
            } catch (Exception e) {
                log.error("Failed to send read response: {}", e.getMessage());
                // WebSocket 실패는 전체 프로세스를 중단하지 않음
            }
            
            return ChatSuccess.READ_MESSAGES_SUCCESS.getMessage();
            
        } catch (RestApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to read messages: roomId={}, memberId={}", 
                    request.roomId(), request.memberId(), e);
            throw new RestApiException(ChatError.MESSAGE_READ_FAILED);
        }
    }

    public ChatSummaryResponse getChatSummaries(LoginMember loginMember) {
        return getChatSummaries(loginMember, null, 20);
    }

    public ChatSummaryResponse getChatSummaries(LoginMember loginMember, Long lastChatRoomId, int limit) {
        try {
            if (loginMember == null || loginMember.memberId() == null) {
                throw new RestApiException(ChatError.INVALID_MEMBER_ID);
            }
            
            List<ChatRoom> chatRooms = getChatRooms(loginMember.memberId(), lastChatRoomId, limit);

            // 중복 방지: 동일한 채팅방이 여러 번 조회되는 경우 id 기준으로 중복 제거
            Map<Long, ChatRoom> uniqueRoomMap = chatRooms.stream()
                    .collect(Collectors.toMap(ChatRoom::getId, Function.identity(), (existing, duplicate) -> existing));
            List<ChatRoom> uniqueChatRooms = new ArrayList<>(uniqueRoomMap.values());

            List<Long> chatRoomIds = uniqueChatRooms.stream().map(ChatRoom::getId).toList();
            List<ChatMessageSummary> chatMessageSummaries = messageRepository.aggregateMessageSummaries(chatRoomIds, loginMember.memberId());
            
            log.debug("Retrieved chat summaries for member: {}, rooms: {}, lastChatRoomId: {}", 
                    loginMember.memberId(), uniqueChatRooms.size(), lastChatRoomId);
            
            return new ChatSummaryResponse(uniqueChatRooms, chatMessageSummaries);
            
        } catch (RestApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get chat summaries for member: {}", loginMember.memberId(), e);
            throw new RestApiException(ChatError.CHAT_ROOM_NOT_FOUND);
        }
    }

    public List<Message> getMessages(Long chatRoomId, String lastMessageId) {
        return getMessages(chatRoomId, lastMessageId, 20);
    }

    public List<Message> getMessages(Long chatRoomId, String lastMessageId, int limit) {
        try {
            if (chatRoomId == null || chatRoomId <= 0) {
                throw new RestApiException(ChatError.INVALID_ROOM_ID);
            }
            
            // 채팅방 존재 확인
            validateChatRoom(chatRoomId);
            
            List<Message> messages = messageRepository.findMessages(chatRoomId, lastMessageId, limit);
            log.debug("Retrieved messages for room: {}, count: {}, lastMessageId: {}", 
                    chatRoomId, messages.size(), lastMessageId);
            
            return messages;
            
        } catch (RestApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get messages for room: {}", chatRoomId, e);
            throw new RestApiException(ChatError.CHAT_ROOM_NOT_FOUND);
        }
    }

    public List<ChatRoom> getChatRooms(Long memberId) {
        return getChatRooms(memberId, null, 20);
    }

    public List<ChatRoom> getChatRooms(Long memberId, Long lastChatRoomId, int limit) {
        try {
            if (memberId == null || memberId <= 0) {
                throw new RestApiException(ChatError.INVALID_MEMBER_ID);
            }
            
            if (limit <= 0 || limit > 100) {
                limit = 20; // 기본값
            }
            
            List<ChatRoom> chatRooms;
            if (lastChatRoomId == null) {
                // 첫 번째 페이지 조회
                Pageable pageable = PageRequest.of(0, limit);
                chatRooms = chatRoomRepository.findByMemberId(memberId);
                // 제한된 수만 반환
                if (chatRooms.size() > limit) {
                    chatRooms = chatRooms.subList(0, limit);
                }
            } else {
                // 무한 스크롤을 위한 다음 페이지 조회
                Pageable pageable = PageRequest.of(0, limit);
                chatRooms = chatRoomRepository.findByMemberIdWithPaging(memberId, lastChatRoomId, pageable);
            }
            
            log.debug("Retrieved chat rooms for member: {}, count: {}, lastChatRoomId: {}", 
                    memberId, chatRooms.size(), lastChatRoomId);
            
            return chatRooms;
            
        } catch (Exception e) {
            log.error("Failed to get chat rooms for member: {}", memberId, e);
            return new ArrayList<>();
        }
    }
    
    private void validateMessageRequest(MessageSendRequest request) {
        if (request == null) {
            throw new RestApiException(ChatError.INVALID_MESSAGE_CONTENT);
        }
        
        if (!StringUtils.hasText(request.content())) {
            throw new RestApiException(ChatError.INVALID_MESSAGE_CONTENT);
        }
        
        if (request.content().length() > 1000) {
            throw new RestApiException(ChatError.MESSAGE_TOO_LONG);
        }
        
        if (request.roomId() == null || request.roomId() <= 0) {
            throw new RestApiException(ChatError.INVALID_ROOM_ID);
        }
        
        if (request.senderId() == null || request.senderId() <= 0) {
            throw new RestApiException(ChatError.INVALID_MEMBER_ID);
        }
        
        if (request.receiverId() == null || request.receiverId() <= 0) {
            throw new RestApiException(ChatError.INVALID_MEMBER_ID);
        }
    }
    
    private void validateReadRequest(ReadRequest request) {
        if (request == null) {
            throw new RestApiException(ChatError.INVALID_ROOM_ID);
        }
        
        if (request.roomId() == null || request.roomId() <= 0) {
            throw new RestApiException(ChatError.INVALID_ROOM_ID);
        }
        
        if (request.memberId() == null || request.memberId() <= 0) {
            throw new RestApiException(ChatError.INVALID_MEMBER_ID);
        }
    }
    
    private ChatRoom validateChatRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RestApiException(ChatError.CHAT_ROOM_NOT_FOUND));
        
        if (chatRoom.getStatus() == ChatRoomStatus.BLOCK) {
            throw new RestApiException(ChatError.CHAT_ROOM_BLOCKED);
        }
        
        if (chatRoom.getStatus() == ChatRoomStatus.WAIT) {
            throw new RestApiException(ChatError.CHAT_ROOM_WAITING);
        }
        
        return chatRoom;
    }
}