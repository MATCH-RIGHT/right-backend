package com.example.rightbackend.sms.domain.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class SmsRepository {

    private final String PREFIX = "sms: ";
    private final int LIMIT_TIME = 3 * 60;
    private final StringRedisTemplate redisTemplate;

    public void createSmsCertification(String phoneNumber, String certificationNumber) {
        redisTemplate.opsForValue()
                .set(PREFIX + phoneNumber, certificationNumber, Duration.ofSeconds(LIMIT_TIME));
    }

    public String getSmsCertification(String phoneNumber) {
        return redisTemplate.opsForValue().get(PREFIX + phoneNumber);
    }

    public void removeSmsCertification(String phoneNumber) {
        redisTemplate.delete(PREFIX + phoneNumber);
    }

    public boolean hasKey(String phoneNumber) {
        return redisTemplate.hasKey(PREFIX + phoneNumber);
    }
}