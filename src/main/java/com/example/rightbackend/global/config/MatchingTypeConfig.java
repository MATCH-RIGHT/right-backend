package com.example.rightbackend.global.config;

import com.example.rightbackend.matching.business.type.FreeMatchingType;
import com.example.rightbackend.matching.business.type.MatchingType;
import com.example.rightbackend.matching.business.type.PremiumMatchingType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MatchingTypeConfig {

    @Bean
    public Map<String, MatchingType> matchingTypes(
            FreeMatchingType freeMatchingType,
            PremiumMatchingType premiumMatchingType) {
        
        Map<String, MatchingType> matchingTypes = new HashMap<>();
        matchingTypes.put(freeMatchingType.getTypeName(), freeMatchingType);
        matchingTypes.put(premiumMatchingType.getTypeName(), premiumMatchingType);
        
        return matchingTypes;
    }
}