package com.example.rightbackend.member.controller;

import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.MemberSuccess;
import com.example.rightbackend.member.controller.dto.request.SignUpRequest;
import com.example.rightbackend.member.service.MemberProfileService;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/member")
@RestController
public class MemberController {
    private final MemberProfileService memberProfileService;

    public MemberController(final MemberProfileService memberProfileService) {
        this.memberProfileService = memberProfileService;
    }

    @PostMapping("/sign-up")
    public SuccessResponse<String> signUp(@RequestBody SignUpRequest request) {
        return SuccessResponse.of(MemberSuccess.SING_UP_SUCCESS, memberProfileService.signUp(request));
    }
}