package com.example.rightbackend.global;

import com.example.rightbackend.global.config.loader.FaceFeatureDataLoader;
import com.example.rightbackend.global.config.loader.InterestDataLoader;
import com.example.rightbackend.global.config.loader.LocationDataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired private DatabaseCleanup databaseCleanup;
    @Autowired private FaceFeatureDataLoader faceFeatureDataLoader;
    @Autowired private InterestDataLoader interestDataLoader;
    @Autowired private LocationDataLoader locationDataLoader;

    @BeforeEach
    void setUp() {
        try {
            databaseCleanup.execute();
            // 데이터 로더 호출 순서 조정 - 의존성이 있는 데이터는 나중에 로드
            try {
                faceFeatureDataLoader.loadFaceFeatureData();
            } catch (Exception e) {
                System.out.println("얼굴 특징 데이터 로드 실패: " + e.getMessage());
            }
            
            try {
                interestDataLoader.loadInterestData();
            } catch (Exception e) {
                System.out.println("관심사 데이터 로드 실패: " + e.getMessage());
            }
            
            try {
                locationDataLoader.loadLocationData();
            } catch (Exception e) {
                System.out.println("위치 데이터 로드 실패: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("테스트 설정 중 오류 발생: " + e.getMessage());
        }
    }
}