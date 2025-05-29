package com.example.rightbackend.matching.business.type;

import com.example.rightbackend.member.domain.MemberProfile;

import java.util.List;

public interface MatchingType {
    List<MemberProfile> executeMatching(MemberProfile memberProfile);
    String getTypeName();
    int calculateCompatibility(MemberProfile sourceProfile, MemberProfile targetProfile);
}
