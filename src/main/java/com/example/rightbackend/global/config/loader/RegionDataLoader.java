package com.example.rightbackend.global.config.loader;

import com.example.rightbackend.matching.filter.domain.Region;
import com.example.rightbackend.matching.filter.domain.repository.RegionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 */
@Slf4j
@Configuration
public class RegionDataLoader {

    private final RegionRepository regionRepository;

    public RegionDataLoader(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }
    
    @Bean(name = "regionDataLoaderRunner")
    public CommandLineRunner initRegionData() {
        return args -> {
            log.info("지역 데이터 초기화 시작");
            loadRegionData();
            log.info("지역 데이터 초기화 완료");
        };
    }

    @Transactional
    public void loadRegionData() {
        saveRegionIfNotExists("수도권", "CAPITAL");
        saveRegionIfNotExists("경상도권", "GYEONGSANG");
        saveRegionIfNotExists("충청권", "CHUNGCHEONG");
        saveRegionIfNotExists("전라권", "JEOLLA");
        saveRegionIfNotExists("강원권", "GANGWON");
        saveRegionIfNotExists("제주권", "JEJU");
    }
    
    private void saveRegionIfNotExists(String name, String code) {
        if (regionRepository.findByCode(code).isEmpty()) {
            regionRepository.save(Region.of(name, code));
        }
    }
}