package com.example.rightbackend.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberProfileToLocation> memberProfileToLocations = new ArrayList<>();

    protected Location() {
    }

    public static Location of(String name) {
        Location location = new Location();
        location.name = name;
        return location;
    }

    public void addMemberProfile(MemberProfile memberProfile) {
        MemberProfileToLocation link = MemberProfileToLocation.of(memberProfile, this);
        this.memberProfileToLocations.add(link);
        memberProfile.getMemberProfileToLocations().add(link);
    }
}