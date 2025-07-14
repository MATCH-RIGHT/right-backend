package com.example.rightbackend.sms.domain.repository;

import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.SmsError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SmsRepository {

    private final String PREFIX = "sms: ";
    private final int LIMIT_TIME = 3 * 60;
    private final StringRedisTemplate redisTemplate;

    public void createSmsCertification(String phoneNumber, String certificationNumber) {
        try {
            redisTemplate.opsForValue()
                    .set(PREFIX + phoneNumber, certificationNumber, Duration.ofSeconds(LIMIT_TIME));
            log.info("SMS certification created for phone number: {}", phoneNumber);
        } catch (Exception e) {
            log.error("Failed to create SMS certification for {}: {}", phoneNumber, e.getMessage());
            throw new RestApiException(SmsError.REDIS_CONNECTION_ERROR);
        }
    }

    public String getSmsCertification(String phoneNumber) {
        try {
            return redisTemplate.opsForValue().get(PREFIX + phoneNumber);
        } catch (Exception e) {
            log.error("Failed to get SMS certification for {}: {}", phoneNumber, e.getMessage());
            throw new RestApiException(SmsError.REDIS_CONNECTION_ERROR);
        }
    }

    public void removeSmsCertification(String phoneNumber) {
        try {
            redisTemplate.delete(PREFIX + phoneNumber);
            log.info("SMS certification removed for phone number: {}", phoneNumber);
        } catch (Exception e) {
            log.error("Failed to remove SMS certification for {}: {}", phoneNumber, e.getMessage());
            throw new RestApiException(SmsError.REDIS_CONNECTION_ERROR);
        }
    }

    public boolean hasKey(String phoneNumber) {
        try {
            Boolean hasKey = redisTemplate.hasKey(PREFIX + phoneNumber);
            return hasKey != null && hasKey;
        } catch (Exception e) {
            log.error("Failed to check key existence for {}: {}", phoneNumber, e.getMessage());
            throw new RestApiException(SmsError.REDIS_CONNECTION_ERROR);
        }
    }
}