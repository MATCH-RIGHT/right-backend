package com.example.rightbackend.auth.controller;

import com.example.rightbackend.auth.controller.dto.AuthToken;
import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.controller.dto.LoginRequest;
import com.example.rightbackend.auth.service.AuthService;
import com.example.rightbackend.global.config.resolver.Login;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.AuthSuccess;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<AuthToken>> login(@RequestBody final LoginRequest request) {
        return SuccessResponse.of(
                AuthSuccess.GENERATE_TOKEN_SUCCESS,
                authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<String>> logout(@RequestBody final AuthToken authToken) {
        return SuccessResponse.of(
                AuthSuccess.LOGOUT_SUCCESS,
                authService.logout(authToken));
    }

    @PostMapping("/reissue")
    public ResponseEntity<SuccessResponse<AuthToken>> reissue(@RequestBody final AuthToken authToken) {
        return SuccessResponse.of(
                AuthSuccess.REISSUE_TOKEN_SUCCESS,
                authService.reissue(authToken));
    }

    @PatchMapping("/withdraw")
    public ResponseEntity<SuccessResponse<String>> withDraw(@Login LoginMember loginMember) {
        return SuccessResponse.of(
                AuthSuccess.WITH_DRAW_SUCCESS,
                authService.withDraw(loginMember));
    }
}