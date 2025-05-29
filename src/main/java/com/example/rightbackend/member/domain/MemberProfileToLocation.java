package com.example.rightbackend.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "memberProfileToLocation")
@Getter @Setter
public class MemberProfileToLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberProfileToLocationId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberProfileId")
    private MemberProfile memberProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locationId")
    private Location location;

    protected MemberProfileToLocation() {
    }

    public static MemberProfileToLocation of(MemberProfile memberProfile, Location location) {
        MemberProfileToLocation link = new MemberProfileToLocation();
        link.memberProfile = memberProfile;
        link.location = location;
        return link;
    }
}
