package com.example.rightbackend.matching.business.controller;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.global.config.resolver.Login;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.error.MatchingError;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.global.response.success.MatchingSuccess;
import com.example.rightbackend.image.service.ImageService;
import com.example.rightbackend.matching.business.controller.dto.response.MatchingResultResponse;
import com.example.rightbackend.matching.business.controller.dto.response.MatchedResponse;
import com.example.rightbackend.matching.business.domain.Matched;
import com.example.rightbackend.matching.business.domain.MatchingResult;
import com.example.rightbackend.matching.business.domain.MatchingType;
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
    private final ImageService imageService;

    @PostMapping("/free")
    public ResponseEntity<SuccessResponse<List<MatchingResultResponse>>> executeFreeMatching(
            @Login LoginMember loginMember) {

        try {
            Member member = getMember(loginMember);
            List<MatchingResult> matchingResults = matchingService.executeMatching(
                    member.getMemberProfile(), MatchingType.FREE.getValue());

            List<MatchingResultResponse> response = matchingResults.stream()
                    .map(matching -> MatchingResultResponse.from(matching, member.getId(), imageService))
                    .collect(Collectors.toList());

            return SuccessResponse.of(MatchingSuccess.EXECUTE_FREE_MATCHING_SUCCESS, response);
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/premium")
    public ResponseEntity<SuccessResponse<List<MatchingResultResponse>>> executePremiumMatching(
            @Login LoginMember loginMember) {

        Member member = getMember(loginMember);
        List<MatchingResult> matchingResults = matchingService.executeMatching(
                member.getMemberProfile(), MatchingType.PREMIUM.getValue());

        List<MatchingResultResponse> response = matchingResults.stream()
                .map(matching -> MatchingResultResponse.from(matching, member.getId(), imageService))
                .collect(Collectors.toList());

        return SuccessResponse.of(MatchingSuccess.EXECUTE_PREMIUM_MATCHING_SUCCESS, response);
    }

    @GetMapping("/active")
    public ResponseEntity<SuccessResponse<List<MatchingResultResponse>>> getActiveMatchings(
            @Login LoginMember loginMember) {

        Member member = getMember(loginMember);
        List<MatchingResult> activeMatchings = matchingService.getActiveMatchings(member.getMemberProfile());

        List<MatchingResultResponse> response = activeMatchings.stream()
                .map(matching -> MatchingResultResponse.from(matching, member.getId(), imageService))
                .collect(Collectors.toList());

        return SuccessResponse.of(MatchingSuccess.GET_ACTIVE_MATCHINGS_SUCCESS, response);
    }

    @GetMapping("/matched")
    public ResponseEntity<SuccessResponse<List<MatchingResultResponse>>> getMatchedResults(
            @Login LoginMember loginMember) {

        Member member = getMember(loginMember);
        List<MatchingResult> matchedResults = matchingService.getMatchedResults(member.getMemberProfile());

        List<MatchingResultResponse> response = matchedResults.stream()
                .map(matching -> MatchingResultResponse.from(matching, member.getId(), imageService))
                .collect(Collectors.toList());

        return SuccessResponse.of(MatchingSuccess.GET_MATCHED_RESULTS_SUCCESS, response);
    }

    @PostMapping("/{matchingResultId}/like")
    public ResponseEntity<SuccessResponse<MatchingResultResponse>> likeMatching(
            @Login LoginMember loginMember,
            @PathVariable Long matchingResultId) {

        if (matchingResultId == null || matchingResultId <= 0) {
            throw new RestApiException(MatchingError.MATCHING_RESULT_NOT_FOUND);
        }

        Member member = getMember(loginMember);
        MatchingResult matchingResult = matchingService.like(matchingResultId, member.getMemberProfile());

        MatchingResultResponse response = MatchingResultResponse.from(matchingResult, member.getId(), imageService);

        return SuccessResponse.of(MatchingSuccess.LIKE_MATCHING_SUCCESS, response);
    }

    @GetMapping("/permanent")
    public ResponseEntity<SuccessResponse<List<MatchedResponse>>> getPermanentMatches(
            @Login LoginMember loginMember) {

        Member member = getMember(loginMember);
        List<Matched> permanentMatches = matchingService.getPermanentMatchesByMemberId(member.getId());

        List<MatchedResponse> response = permanentMatches.stream()
                .map(matched -> MatchedResponse.from(matched, member.getId(), imageService))
                .collect(Collectors.toList());

        return SuccessResponse.of(MatchingSuccess.GET_PERMANENT_MATCHES_SUCCESS, response);
    }

    private Member getMember(LoginMember loginMember) {
        return memberRepository.findById(loginMember.memberId())
                .orElseThrow(() -> new RestApiException(MemberError.NULL_MEMBER));
    }
    
//    /**
//     * 매칭 ID로 채팅방을 생성하거나 조회합니다.
//     * 이미 채팅방이 존재하면 기존 채팅방을 반환하고, 없으면 새로 생성합니다.
//     *
//     * @param loginMember 로그인한 회원 정보
//     * @param matchedId 매칭 ID
//     * @return 채팅방 정보
//     */
//    @PostMapping("/matched/{matchedId}/chat")
//    public ResponseEntity<SuccessResponse<ChatRoomResponse>> createOrGetChatRoom(
//            @Login LoginMember loginMember,
//            @PathVariable Long matchedId) {
//
//        Member member = getMember(loginMember);
//        ChatRoomResponse chatRoomResponse = matchedChatService.getOrCreateChatRoomByMatchedId(matchedId);
//
//        return SuccessResponse.of(MatchingSuccess.CREATE_CHAT_ROOM_SUCCESS, chatRoomResponse);
//    }
//
//    /**
//     * 로그인한 회원의 모든 채팅방 목록을 조회합니다.
//     *
//     * @param loginMember 로그인한 회원 정보
//     * @return 채팅방 목록
//     */
//    @GetMapping("/chat/rooms")
//    public ResponseEntity<SuccessResponse<List<ChatRoomResponse>>> getChatRooms(
//            @Login LoginMember loginMember) {
//
//        Member member = getMember(loginMember);
//        List<ChatRoomResponse> chatRooms = matchedChatService.getChatRoomsByMemberProfileId(member.getMemberProfile().getId());
//
//        return SuccessResponse.of(MatchingSuccess.GET_CHAT_ROOMS_SUCCESS, chatRooms);
//    }
}