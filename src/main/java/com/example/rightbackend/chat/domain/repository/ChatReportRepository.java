package com.example.rightbackend.chat.domain.repository;

import com.example.rightbackend.chat.domain.ChatReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatReportRepository extends JpaRepository<ChatReport, Long> {
}