package com.example.rightbackend.docs;

import com.example.rightbackend.auth.controller.AuthController;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.global.DummyGenerator;
import com.example.rightbackend.image.controller.ImageController;
import com.example.rightbackend.member.controller.MemberController;
import com.example.rightbackend.sms.controller.SmsController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseRestDocsTest {
    @SpyBean
    protected AuthController authController;
    @SpyBean
    protected MemberController memberController;
    @SpyBean
    protected SmsController smsController;
    @SpyBean
    protected ImageController imageController;

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected DummyGenerator dummyGenerator;

    protected Member member;
    protected String GIVEN_ACCESS_TOKEN;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        member = dummyGenerator.generateSingleMember();
        GIVEN_ACCESS_TOKEN = dummyGenerator.generateAccessToken(member);
    }
}