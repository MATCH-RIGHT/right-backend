package com.example.rightbackend.noti.domain.repository;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.noti.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findNotificationsByMemberOrderByCreatedAtDesc(Member member);
    List<Notification> findNotificationsByMemberIdOrderByCreatedAtDesc(Long memberId);
    Long countByMemberIdAndIsReadFalse(Long memberId);
}
