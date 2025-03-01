package com.example.rightbackend.auth.domain;

import com.example.rightbackend.member.controller.dto.EncodeMember;
import com.example.rightbackend.member.domain.MemberProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column
    private String provider;

    @Column
    private String provider_id;

    @Column
    private String password;

    @Column
    private String phone_number;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id")
    private MemberProfile memberProfile;

    protected Member () {
    }

    public static Member of(final EncodeMember request) {
        Member member = new Member();
        member.provider = request.provider();
        member.provider_id = request.provider_id();
        member.password = request.password();
        member.phone_number = request.phoneNumber();
        return member;
    }
}