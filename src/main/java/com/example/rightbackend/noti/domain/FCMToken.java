package com.example.rightbackend.noti.domain;

import com.example.rightbackend.auth.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class FCMToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcmTokenId")
    private Long id;

    @OneToOne
    @JoinColumn(name = "memberId")
    private Member member;

    @Column
    private String token;

    @Column
    private Boolean isAccepted = true;

    protected FCMToken(Member member, String token) {
        this.member = member;
        this.token = token;
    }

    protected FCMToken() {}

    public static FCMToken of(Member member, String token) {
        return new FCMToken(member, token);
    }
    
    public void changeToken(String token) {
        this.token = token;
    }

    public Boolean isAccepted() { return isAccepted; }
    
    public void changeAccept(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
}