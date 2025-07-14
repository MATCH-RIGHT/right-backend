package com.example.rightbackend.chat.domain;

import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.ChatError;

public enum ChatReportCategory {
    SEXUAL, VIOLENCE, SPAM, SCAM, ETC;

    public static ChatReportCategory fromString(String category) {
        for (ChatReportCategory reportCategory : ChatReportCategory.values()) {
            if (reportCategory.name().equalsIgnoreCase(category)) {
                return reportCategory;
            }
        }
        throw new RestApiException(ChatError.INVALID_REPORT_CATEGORY);
    }
}