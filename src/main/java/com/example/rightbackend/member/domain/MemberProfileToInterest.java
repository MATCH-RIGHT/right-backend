package com.example.rightbackend.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "memberProfileToInterest")
public class MemberProfileToInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberProfileToInterestId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memberProfileId")
    private MemberProfile memberProfile;

    @ManyToOne
    @JoinColumn(name = "interest_id")
    private Interest interest;

    protected MemberProfileToInterest() {}

    public static MemberProfileToInterest of(MemberProfile memberProfile, Interest interest) {
        MemberProfileToInterest link = new MemberProfileToInterest();
        link.interest = interest;
        link.memberProfile = memberProfile;
        return link;
    }
}