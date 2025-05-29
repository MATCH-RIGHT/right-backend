package com.example.rightbackend.docs;

import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.SmsSuccess;
import com.example.rightbackend.sms.controller.dto.request.SmsConfirmRequest;
import com.example.rightbackend.sms.controller.dto.request.SmsSendRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SmsRestDocs extends BaseRestDocsTest {

    @Test
    @DisplayName("API - SMS 인증코드 전송")
    void sendSms() throws Exception {
        final SmsSendRequest request = new SmsSendRequest("01012345678");
        final String message = SmsSuccess.VERIFICATION_CODE_SEND_SUCCESS.getMessage();
        SuccessResponse<String> response = SuccessResponse.of(SmsSuccess.VERIFICATION_CODE_SEND_SUCCESS, message);

        doReturn(response).when(smsController).sendSms(any());

        this.mockMvc.perform(post("/api/sms/send")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("sms-send",
                        requestFields(
                                fieldWithPath("phoneNumber").description("전화번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("인증코드 전송 결과")
                        )
                ));
    }

    @Test
    @DisplayName("API - SMS 인증코드 확인")
    void confirmSms() throws Exception {
        final SmsConfirmRequest request = new SmsConfirmRequest("01012345678", "123456");
        final String message = SmsSuccess.EQUAL_VERIFICATION_CODE_SUCCESS.getMessage();
        SuccessResponse<String> response = SuccessResponse.of(SmsSuccess.EQUAL_VERIFICATION_CODE_SUCCESS, message);

        doReturn(response).when(smsController).SmsVerification(any());

        this.mockMvc.perform(post("/api/sms/confirm")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("sms-confirm",
                        requestFields(
                                fieldWithPath("phoneNumber").description("전화번호"),
                                fieldWithPath("certificationNumber").description("인증 코드")
                        ),
                        responseFields(
                                fieldWithPath("code").description("성공 코드"),
                                fieldWithPath("result").description("인증코드 확인 결과")
                        )
                ));
    }
}