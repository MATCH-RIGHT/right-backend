package com.example.rightbackend.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "member_profile_to_interest")
public class MemberProfileToInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_profile_to_interest_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_profile_id")
    private MemberProfile memberProfile;

    @ManyToOne
    @JoinColumn(name = "interest_name")
    private Interest interest;


    protected MemberProfileToInterest() {}

    public static MemberProfileToInterest of(MemberProfile memberProfile, Interest interest) {
        MemberProfileToInterest link = new MemberProfileToInterest();
        link.interest = interest;
        link.memberProfile = memberProfile;
        return link;
    }
}