package com.example.rightbackend.global.response.success;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 매칭 관련 성공 응답 코드 정의
 */
@Getter
@RequiredArgsConstructor
public enum MatchingSuccess implements SuccessCode {

    // 매칭 조회
    GET_ACTIVE_MATCHINGS_SUCCESS(HttpStatus.OK, "활성화된 매칭 목록 조회에 성공했습니다."),
    GET_MATCHED_RESULTS_SUCCESS(HttpStatus.OK, "매칭된 결과 목록 조회에 성공했습니다."),
    
    // 매칭 실행
    EXECUTE_FREE_MATCHING_SUCCESS(HttpStatus.OK, "무료 매칭이 성공적으로 실행되었습니다."),
    EXECUTE_PREMIUM_MATCHING_SUCCESS(HttpStatus.OK, "유료 매칭이 성공적으로 실행되었습니다."),
    
    // 매칭 액션
    LIKE_MATCHING_SUCCESS(HttpStatus.OK, "매칭에 좋아요를 보냈습니다.");

    private final HttpStatus httpStatus;
    private final String message;
    
    @Override
    public String getCode() {
        return this.name();
    }
}
