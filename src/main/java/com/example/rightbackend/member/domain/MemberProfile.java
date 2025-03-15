package com.example.rightbackend.member.domain;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.member.controller.dto.EncodeMemberPage;
import com.example.rightbackend.member.controller.dto.EncodeMemberProfile;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "member_profile")
@Getter @Setter
public class MemberProfile implements Persistable<Long> {

    @Id
    @Column(name = "member_profile_id")
    private Long id;

    @Column
    private String nickname;

    @Column
    private String gender;

    @Column
    private String birthday;

    @Column
    private String address;

    @Column
    private String height;

    @Column
    private String body_type;

    @Column
    private String job;

    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberProfileToInterest> memberProfileToInterests = new ArrayList<>();

    @Column
    private String money = "0";

    @Column
    private String myself;

    protected MemberProfile() {
    }

    public static MemberProfile of(EncodeMemberProfile request, Member member) {
        MemberProfile memberProfile = new MemberProfile();
        memberProfile.id = member.getId();
        memberProfile.nickname = request.nickname();
        memberProfile.gender = request.gender();
        memberProfile.birthday = request.birthday();
        memberProfile.address = request.address();
        memberProfile.height = request.height();
        memberProfile.body_type = request.body_type();
        memberProfile.job = request.job();
        memberProfile.myself = request.myself();
        return memberProfile;
    }

    public EncodeMemberPage getMemberPage() {
        List<String> interests = memberProfileToInterests.stream()
                .map(link -> link.getInterest().getName())
                .collect(Collectors.toList());
        return new EncodeMemberPage(nickname, address, height, body_type, job, interests, myself);
    }

    public void addInterest(Interest interest) {
        MemberProfileToInterest link = MemberProfileToInterest.of(this, interest);
        this.memberProfileToInterests.add(link);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}