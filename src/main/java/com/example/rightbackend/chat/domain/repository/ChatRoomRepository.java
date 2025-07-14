package com.example.rightbackend.chat.domain.repository;

import com.example.rightbackend.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT DISTINCT c FROM ChatRoom c JOIN c.participants p WHERE p.member.id = :memberId AND c.status = com.example.rightbackend.chat.domain.ChatRoomStatus.OPEN ORDER BY c.id DESC")
    List<ChatRoom> findByMemberId(Long memberId);

    @Query("SELECT DISTINCT c FROM ChatRoom c JOIN c.participants p WHERE p.member.id = :memberId AND c.status = com.example.rightbackend.chat.domain.ChatRoomStatus.OPEN AND c.id < :lastChatRoomId ORDER BY c.id DESC")
    List<ChatRoom> findByMemberIdWithPaging(Long memberId, Long lastChatRoomId, org.springframework.data.domain.Pageable pageable);

    @Modifying
    @Query("UPDATE ChatRoom c SET c.status = com.example.rightbackend.chat.domain.ChatRoomStatus.CLOSE")
    void expireAllChatRooms();

    @Modifying
    @Query("UPDATE ChatRoom c SET c.status = com.example.rightbackend.chat.domain.ChatRoomStatus.OPEN WHERE c.status = com.example.rightbackend.chat.domain.ChatRoomStatus.WAIT")
    void openWaitingChatRooms();
}