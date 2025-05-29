package com.example.rightbackend.matching.filter.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "region")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;
    
    @Column(name = "region_partition")
    private String partition;

    protected Region() {}

    public static Region of(String name, String code) {
        Region region = new Region();
        region.name = name;
        region.code = code;
        region.partition = code;
        return region;
    }
    
    public String getPartition() {
        return partition;
    }
}
