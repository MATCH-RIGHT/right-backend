package com.example.rightbackend.matching.business.controller;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.global.config.resolver.Login;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.global.response.success.MatchingSuccess;
import com.example.rightbackend.matching.business.controller.dto.response.MatchingResultResponse;
import com.example.rightbackend.matching.business.domain.MatchingResult;
import com.example.rightbackend.matching.business.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;
    private final MemberRepository memberRepository;

    private static final String FREE_MATCHING_TYPE = "FREE";
    private static final String PREMIUM_MATCHING_TYPE = "PREMIUM";

    @PostMapping("/free")
    public ResponseEntity<SuccessResponse<List<MatchingResultResponse>>> executeFreeMatching(
            @Login LoginMember loginMember) {

        Member member = getMember(loginMember);
        List<MatchingResult> matchingResults = matchingService.executeMatching(
                member.getMemberProfile(), FREE_MATCHING_TYPE);

        List<MatchingResultResponse> response = matchingResults.stream()
                .map(matching -> MatchingResultResponse.from(matching, member.getId()))
                .collect(Collectors.toList());

        return SuccessResponse.of(MatchingSuccess.EXECUTE_FREE_MATCHING_SUCCESS, response);
    }

    @PostMapping("/premium")
    public ResponseEntity<SuccessResponse<List<MatchingResultResponse>>> executePremiumMatching(
            @Login LoginMember loginMember) {

        Member member = getMember(loginMember);
        List<MatchingResult> matchingResults = matchingService.executeMatching(
                member.getMemberProfile(), PREMIUM_MATCHING_TYPE);

        List<MatchingResultResponse> response = matchingResults.stream()
                .map(matching -> MatchingResultResponse.from(matching, member.getId()))
                .collect(Collectors.toList());

        return SuccessResponse.of(MatchingSuccess.EXECUTE_PREMIUM_MATCHING_SUCCESS, response);
    }

    @GetMapping("/active")
    public ResponseEntity<SuccessResponse<List<MatchingResultResponse>>> getActiveMatchings(
            @Login LoginMember loginMember) {

        Member member = getMember(loginMember);
        List<MatchingResult> activeMatchings = matchingService.getActiveMatchings(member.getMemberProfile());

        List<MatchingResultResponse> response = activeMatchings.stream()
                .map(matching -> MatchingResultResponse.from(matching, member.getId()))
                .collect(Collectors.toList());

        return SuccessResponse.of(MatchingSuccess.GET_ACTIVE_MATCHINGS_SUCCESS, response);
    }

    @GetMapping("/matched")
    public ResponseEntity<SuccessResponse<List<MatchingResultResponse>>> getMatchedResults(
            @Login LoginMember loginMember) {

        Member member = getMember(loginMember);
        List<MatchingResult> matchedResults = matchingService.getMatchedResults(member.getMemberProfile());

        List<MatchingResultResponse> response = matchedResults.stream()
                .map(matching -> MatchingResultResponse.from(matching, member.getId()))
                .collect(Collectors.toList());

        return SuccessResponse.of(MatchingSuccess.GET_MATCHED_RESULTS_SUCCESS, response);
    }

    @PostMapping("/{matchingResultId}/like")
    public ResponseEntity<SuccessResponse<MatchingResultResponse>> likeMatching(
            @Login LoginMember loginMember,
            @PathVariable Long matchingResultId) {

        Member member = getMember(loginMember);
        MatchingResult matchingResult = matchingService.like(matchingResultId, member.getMemberProfile());

        MatchingResultResponse response = MatchingResultResponse.from(matchingResult, member.getId());

        return SuccessResponse.of(MatchingSuccess.LIKE_MATCHING_SUCCESS, response);
    }

    private Member getMember(LoginMember loginMember) {
        return memberRepository.findById(loginMember.memberId())
                .orElseThrow(() -> new RestApiException(MemberError.NULL_MEMBER));
    }
}