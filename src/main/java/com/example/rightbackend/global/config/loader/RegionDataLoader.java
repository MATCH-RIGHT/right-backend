package com.example.rightbackend.global.config.loader;

import com.example.rightbackend.matching.filter.domain.Region;
import com.example.rightbackend.matching.filter.domain.repository.RegionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Component
@RequiredArgsConstructor
public class RegionDataLoader {

    private final RegionRepository regionRepository;

    @PostConstruct
    @Transactional
    public void init() {
        log.info("지역 데이터 초기화 시작");
        loadRegionData();
        log.info("지역 데이터 초기화 완료");
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
