package com.example.rightbackend.global.config.loader;

import com.example.rightbackend.matching.filter.domain.Region;
import com.example.rightbackend.matching.filter.domain.repository.RegionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionDataLoaderTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionDataLoader regionDataLoader;

    @Test
    void loadRegionData_shouldSaveRegions_whenRegionsDoNotExist() {
        // given
        when(regionRepository.findByCode(anyString())).thenReturn(Optional.empty());
        
        // when
        regionDataLoader.loadRegionData();
        
        // then
        verify(regionRepository, times(6)).save(any(Region.class));
    }
    
    @Test
    void loadRegionData_shouldNotSaveRegions_whenRegionsAlreadyExist() {
        // given
        Region existingRegion = Region.of("수도권", "CAPITAL");
        
        when(regionRepository.findByCode(anyString())).thenReturn(Optional.of(existingRegion));
        
        // when
        regionDataLoader.loadRegionData();
        
        // then
        verify(regionRepository, never()).save(any(Region.class));
    }
}
