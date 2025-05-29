package com.example.rightbackend.notification.domain;

import com.example.rightbackend.auth.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class FcmToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcmTokenId")
    private Long id;

    @OneToOne
    @JoinColumn(name = "memberId")
    private Member member;

    @Column
    private String token;

    @Column
    private Boolean isExpired = true;

    protected FcmToken() {
    }

    public FcmToken(Member member, String token) {
        this.member = member;
        this.token = token;
    }

    public static FcmToken of(Member member, String token) {
        return new FcmToken(member, token);
    }
}