package com.example.rightbackend.auth.domain;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.member.controller.dto.EncodeMember;
import com.example.rightbackend.member.controller.dto.EncodeMemberPage;
import com.example.rightbackend.member.controller.dto.response.MemberPageResponse;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.service.TextEncoder;
import com.example.rightbackend.uploader.domain.MemberImage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_Id")
    private Long id;

    @Column
    private String name;

    @Column
    private String provider;

    @Column
    private String providerId;

    @Column
    private String password;

    @Column
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column
    private MemberRole role = MemberRole.MEMBER;

    @Column
    private Boolean withdraw = false;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id")
    private MemberProfile memberProfile;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "memberImageId")
    private List<MemberImage> memberImage = new ArrayList<>();

    public LoginMember getLoginMember() {
        return new LoginMember(id, role);
    }

    public MemberPageResponse getMemberPageResopnse() {
        EncodeMemberPage encodeMemberPage = memberProfile.getMemberPage();
        return new MemberPageResponse(name,
                encodeMemberPage.nickname(),
                TextEncoder.decrypt(encodeMemberPage.address()),
                TextEncoder.decrypt(encodeMemberPage.height()),
                TextEncoder.decrypt(encodeMemberPage.body_type()),
                TextEncoder.decrypt(encodeMemberPage.job()),
                encodeMemberPage.interests(),
                TextEncoder.decrypt(encodeMemberPage.myself()));
    }

    protected Member () {
    }

    public static Member of(final EncodeMember request) {
        Member member = new Member();
        member.provider = request.provider();
        member.providerId = request.providerId();
        member.password = request.password();
        member.phoneNumber = request.phoneNumber();
        return member;
    }
}