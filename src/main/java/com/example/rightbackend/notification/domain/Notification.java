package com.example.rightbackend.notification.domain;

import com.example.rightbackend.auth.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notificationId")
    private Long id;

    @Column
    private String content;

    @Column
    private boolean isRead = false;

    @Column(updatable = false)
    @CreatedDate
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    protected Notification() {
    }

}