package com.example.rightbackend.member.controller;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.global.config.resolver.Login;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.MemberSuccess;
import com.example.rightbackend.member.controller.dto.request.CheckIdRequest;
import com.example.rightbackend.member.controller.dto.request.ResetPasswordRequest;
import com.example.rightbackend.member.controller.dto.request.SearchIdRequest;
import com.example.rightbackend.member.controller.dto.request.SignUpRequest;
import com.example.rightbackend.member.controller.dto.response.MemberPageResponse;
import com.example.rightbackend.member.service.MemberProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/member")
@RestController
public class MemberController {
    private final MemberProfileService memberProfileService;

    public MemberController(final MemberProfileService memberProfileService) {
        this.memberProfileService = memberProfileService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<SuccessResponse<String>> signUp(@RequestBody SignUpRequest request) {
        return SuccessResponse.of(MemberSuccess.SIGN_UP_SUCCESS, memberProfileService.signUp(request));
    }

    @PostMapping("/check-id")
    public ResponseEntity<SuccessResponse<String>> checkId(@RequestBody CheckIdRequest givenId) {
        return SuccessResponse.of(MemberSuccess.CHECK_ID_SUCCESS, memberProfileService.checkDuplicateId(givenId));
    }

    @GetMapping("/member-page")
    public ResponseEntity<SuccessResponse<MemberPageResponse>> getMemberPage(@Login LoginMember loginMember) {
        return SuccessResponse.of(
                MemberSuccess.GET_MEMBER_PAGE_SUCCESS,
                memberProfileService.getMemberPage(loginMember));
    }

    @PostMapping("/search-id")
    public ResponseEntity<SuccessResponse<String>> searchId(@RequestBody SearchIdRequest request) {
        return SuccessResponse.of(MemberSuccess.SEARCH_ID_SUCCESS, memberProfileService.searchId(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<SuccessResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        return SuccessResponse.of(MemberSuccess.CHANGE_PASSWORD_SUCCESS, memberProfileService.resetPassword(request));
    }
}