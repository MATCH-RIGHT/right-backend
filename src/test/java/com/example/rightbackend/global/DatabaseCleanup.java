package com.example.rightbackend.global;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseCleanup implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        tableNames = new ArrayList<>();
        tableNames.add("member");
        tableNames.add("interest");
        tableNames.add("location");
        tableNames.add("face_feature");
        tableNames.add("my_face_feature");
        tableNames.add("matching_filter");
        tableNames.add("member_profile");
        tableNames.add("member_image");
        // 필요한 테이블 이름을 직접 추가
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        try {
            entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
            
            for (String tableName : tableNames) {
                try {
                    entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
                } catch (Exception e) {
                    // 테이블이 존재하지 않거나 다른 이유로 실패할 경우 계속 진행
                    System.out.println("테이블 초기화 실패: " + tableName + " - " + e.getMessage());
                }
            }
            
            entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        } catch (Exception e) {
            // H2 데이터베이스가 아닌 경우 대체 로직 실행
            System.out.println("데이터베이스 초기화 중 오류 발생: " + e.getMessage());
            for (String tableName : tableNames) {
                try {
                    entityManager.createNativeQuery("DELETE FROM " + tableName).executeUpdate();
                } catch (Exception ex) {
                    // 무시하고 계속 진행
                }
            }
        }
    }
}