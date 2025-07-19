package com.example.rightbackend.auth.domain;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.member.controller.dto.EncodeMember;
import com.example.rightbackend.member.controller.dto.EncodeMemberPage;
import com.example.rightbackend.member.controller.dto.response.MemberPageResponse;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.service.TextEncoder;
import com.example.rightbackend.image.domain.MemberImage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_name_phone", columnNames = {"name", "phoneNumber"})
})
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId")
    private Long id;

    @Column
    private String name;


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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "memberProfileId")
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
                TextEncoder.decrypt(encodeMemberPage.locationName()),
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
        member.name = request.name();
        member.providerId = request.providerId();
        member.password = request.password();
        member.phoneNumber = request.phoneNumber();
        return member;
    }
}