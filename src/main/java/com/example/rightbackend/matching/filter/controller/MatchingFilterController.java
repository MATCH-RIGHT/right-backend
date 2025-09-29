package com.example.rightbackend.matching.filter.controller;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.global.config.resolver.Login;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.MatchingFilterSuccess;
import com.example.rightbackend.matching.filter.controller.dto.request.MatchingFilterRequest;
import com.example.rightbackend.matching.filter.controller.dto.request.MatchingFilterIdsRequest;
import com.example.rightbackend.matching.filter.controller.dto.response.MatchingFilterResponse;
import com.example.rightbackend.matching.filter.controller.dto.response.MatchingFilterIdsResponse;
import com.example.rightbackend.matching.filter.domain.MatchingFilter;
import com.example.rightbackend.matching.filter.service.MatchingFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matching/filter")
public class MatchingFilterController {

    private final MatchingFilterService matchingFilterService;

    @GetMapping
    public ResponseEntity<SuccessResponse<MatchingFilterResponse>> getMatchingFilter(@Login LoginMember loginMember) {
        MatchingFilter matchingFilter = matchingFilterService.getMatchingFilter(loginMember);
        return SuccessResponse.of(MatchingFilterSuccess.GET_MATCHING_FILTER_SUCCESS, MatchingFilterResponse.from(matchingFilter));
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<MatchingFilterResponse>> createOrUpdateMatchingFilter(
            @Login LoginMember loginMember,
            @RequestBody MatchingFilterRequest request) {
        MatchingFilter matchingFilter = matchingFilterService.createOrUpdateMatchingFilter(loginMember, request);
        return SuccessResponse.of(MatchingFilterSuccess.UPDATE_MATCHING_FILTER_SUCCESS, MatchingFilterResponse.from(matchingFilter));
    }

    @GetMapping("/v2")
    public ResponseEntity<SuccessResponse<MatchingFilterIdsResponse>> getMatchingFilterWithIds(@Login LoginMember loginMember) {
        MatchingFilter matchingFilter = matchingFilterService.getMatchingFilter(loginMember);
        return SuccessResponse.of(MatchingFilterSuccess.GET_MATCHING_FILTER_SUCCESS, MatchingFilterIdsResponse.from(matchingFilter));
    }

    @PostMapping("/v2")
    public ResponseEntity<SuccessResponse<MatchingFilterIdsResponse>> createOrUpdateMatchingFilterWithIds(
            @Login LoginMember loginMember,
            @RequestBody MatchingFilterIdsRequest request) {
        MatchingFilter matchingFilter = matchingFilterService.createOrUpdateMatchingFilterWithIds(loginMember, request);
        return SuccessResponse.of(MatchingFilterSuccess.UPDATE_MATCHING_FILTER_SUCCESS, MatchingFilterIdsResponse.from(matchingFilter));
    }
}