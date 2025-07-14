package com.example.rightbackend.noti.controller.dto.request;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.noti.domain.NotificationCategory;

import java.util.List;

public record NotificationRequest(NotificationCategory category,
                                  Long relatedId,
                                  String content,
                                  List<Member> members) {
}
