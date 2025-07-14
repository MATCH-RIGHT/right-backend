package com.example.rightbackend.docs;

import com.example.rightbackend.chat.controller.dto.request.MessageSendRequest;
import com.example.rightbackend.chat.controller.dto.response.ChatSummaryResponse;
import com.example.rightbackend.chat.domain.ChatRoom;
import com.example.rightbackend.chat.domain.ChatRoomStatus;
import com.example.rightbackend.chat.domain.Message;
import com.example.rightbackend.chat.domain.model.ChatMessageSummary;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.ChatSuccess;
import com.example.rightbackend.global.response.error.ChatError;
import com.example.rightbackend.global.exception.RestApiException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChatRestDocs extends BaseRestDocsTest {

    @Test
    @DisplayName("API - 채팅 메시지 조회")
    void getMessages() throws Exception {
        Long chatRoomId = 1L;
        MessageSendRequest req1 = new MessageSendRequest("첫 번째 메시지", chatRoomId, 101L, 102L);
        MessageSendRequest req2 = new MessageSendRequest("두 번째 메시지", chatRoomId, 101L, 102L);

        Message message1 = Message.from(req1);
        Message message2 = Message.from(req2);

        List<Message> result = List.of(message1, message2);
        SuccessResponse<List<Message>> response = SuccessResponse.of(ChatSuccess.GET_MESSAGES_SUCCESS, result);
        doReturn(response).when(chatController).getMessage(any(), any(), anyInt());

        mockMvc.perform(get("/chat/room/{roomId}/messages", chatRoomId)
                        .param("lastMessageId", "60f3b2...")
                        .param("limit", "20"))
                .andExpect(status().isOk())
                .andDo(document("chat-get-messages",
                        pathParameters(parameterWithName("roomId").description("채팅방 ID")),
                        queryParameters(
                                parameterWithName("lastMessageId").description("마지막 메시지 ID (무한 스크롤용)").optional(),
                                parameterWithName("limit").description("조회할 메시지 수 (기본: 20, 최대: 100)").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("result").description("메시지 목록 결과"),
                                fieldWithPath("result[].id").description("메시지 ID"),
                                fieldWithPath("result[].content").description("내용"),
                                fieldWithPath("result[].roomId").description("채팅방 ID"),
                                fieldWithPath("result[].senderId").description("보낸 사람 ID"),
                                fieldWithPath("result[].receiverId").description("받는 사람 ID"),
                                fieldWithPath("result[].sendTime").description("보낸 시간"),
                                fieldWithPath("result[].isRead").description("읽음 여부")
                        )
                ));
    }

    @Test
    @DisplayName("API - 채팅 메시지 조회 실패 (채팅방 없음)")
    void getMessages_NotFound() throws Exception {
        Long chatRoomId = 999L;

        // 예외 발생 설정
        doThrow(new RestApiException(ChatError.CHAT_ROOM_NOT_FOUND))
                .when(chatController).getMessage(any(), any(), anyInt());

        mockMvc.perform(get("/chat/room/{roomId}/messages", chatRoomId))
                .andExpect(status().isNotFound())
                .andDo(document("chat-get-messages-404",
                        pathParameters(parameterWithName("roomId").description("채팅방 ID"))
                ));
    }

    @Test
    @DisplayName("API - 채팅 요약 정보 조회")
    void getSummaries() throws Exception {
        ChatRoom room1 = mock(ChatRoom.class);
        when(room1.getId()).thenReturn(1L);
        when(room1.getStatus()).thenReturn(ChatRoomStatus.OPEN);
        ChatRoom room2 = mock(ChatRoom.class);
        when(room2.getId()).thenReturn(2L);
        when(room2.getStatus()).thenReturn(ChatRoomStatus.OPEN);
        List<ChatRoom> chatRooms = List.of(room1, room2);

        Date now = new Date();
        List<ChatMessageSummary> summaries = List.of(
                new ChatMessageSummary(1L, "첫 방 마지막", now, 4L),
                new ChatMessageSummary(2L, "두 방 마지막", new Date(now.getTime() - 3600000), 2L)
        );

        ChatSummaryResponse result = new ChatSummaryResponse(chatRooms, summaries);
        SuccessResponse<ChatSummaryResponse> response = SuccessResponse.of(ChatSuccess.GET_SUMMARIES_SUCCESS, result);
        doReturn(response).when(chatController).getSummaries(any(), any(), anyInt());

        mockMvc.perform(get("/chat/summaries")
                        .param("lastChatRoomId", "1")
                        .param("limit", "20")
                        .header("Authorization", GIVEN_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("chat-get-summaries",
                        queryParameters(
                                parameterWithName("lastChatRoomId").description("마지막 채팅방 ID (무한 스크롤용)").optional(),
                                parameterWithName("limit").description("조회할 채팅방 수 (기본: 20, 최대: 100)").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("result.chatRooms").description("사용자 채팅방 목록"),
                                fieldWithPath("result.chatRooms[].id").description("채팅방 ID"),
                                fieldWithPath("result.chatRooms[].status").description("채팅방 상태"),
                                fieldWithPath("result.chatRooms[].participants").ignored(),                                
                                fieldWithPath("result.chatMessageSummaries").description("채팅방 요약 정보 목록"),
                                fieldWithPath("result.chatMessageSummaries[].roomId").description("채팅방 ID"),
                                fieldWithPath("result.chatMessageSummaries[].lastMessageContent").description("마지막 메시지"),
                                fieldWithPath("result.chatMessageSummaries[].lastMessageTime").description("마지막 메시지 시간"),
                                fieldWithPath("result.chatMessageSummaries[].numberOfUnReadMessages").description("안 읽은 메시지 수")
                        )
                ));
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(chatController);
    }
}