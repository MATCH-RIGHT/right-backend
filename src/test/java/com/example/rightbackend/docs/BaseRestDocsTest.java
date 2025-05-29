package com.example.rightbackend.docs;

import com.example.rightbackend.auth.controller.AuthController;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.global.DummyGenerator;
import com.example.rightbackend.global.config.loader.FaceFeatureDataLoader;
import com.example.rightbackend.global.config.loader.InterestDataLoader;
import com.example.rightbackend.global.config.loader.LocationDataLoader;
import com.example.rightbackend.image.controller.ImageController;
import com.example.rightbackend.matching.business.controller.MatchingController;
import com.example.rightbackend.matching.filter.controller.MatchingFilterController;
import com.example.rightbackend.member.controller.MemberController;
import com.example.rightbackend.member.domain.repository.InterestRepository;
import com.example.rightbackend.member.domain.repository.LocationRepository;
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
    @SpyBean
    protected MatchingFilterController matchingFilterController;
    @SpyBean
    protected InterestDataLoader interestDataLoader;
    @SpyBean
    protected InterestRepository interestRepository;
    @SpyBean
    protected LocationDataLoader locationDataLoader;
    @SpyBean
    protected LocationRepository locationRepository;
    @SpyBean
    protected FaceFeatureDataLoader faceFeatureDataLoader;
    @SpyBean
    protected MatchingController matchingController;

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected DummyGenerator dummyGenerator;

    protected Member member;
    protected String GIVEN_ACCESS_TOKEN;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        try {
            try {
                interestDataLoader.loadInterestData();
                locationDataLoader.loadLocationData();
                faceFeatureDataLoader.loadFaceFeatureData();
            } catch (Exception e) {
                System.out.println("데이터 로딩 중 오류 발생: " + e.getMessage());
            }
            
            member = dummyGenerator.generateSingleMember();
            GIVEN_ACCESS_TOKEN = dummyGenerator.generateAccessToken(member);
        } catch (Exception e) {
            System.out.println("setUp 중 오류 발생: " + e.getMessage());
        }
    }
}