package com.example.rightbackend.auth.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity @Table(name = "jwtToken")
@Getter @Setter
public class Token {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jwtTokenId")
    private Long id;

    @Column
    private String refreshToken;

    @Column
    private LocalDateTime recentLogin = LocalDateTime.now();

    @Column
    private boolean isExpired = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    protected Token() {
    }

    public Token(Member member) {
        this.member = member;
    }

    public void changeRecentLogin() {
        this.recentLogin = LocalDateTime.now();
    }
}