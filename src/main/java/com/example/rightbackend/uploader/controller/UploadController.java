package com.example.rightbackend.uploader.controller;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.global.config.resolver.Login;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.MemberSuccess;
import com.example.rightbackend.uploader.controller.dto.response.UploadResponse;
import com.example.rightbackend.uploader.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/prepare")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/multiImage")
    public ResponseEntity<SuccessResponse<UploadResponse>> uploadMultiImage(@Login final LoginMember loginMember,
                                                                            @RequestPart final List<MultipartFile> images) {
        uploadService.multiUpload(loginMember, images);
        return SuccessResponse.of(MemberSuccess.IMAGE_UPLOAD_SUCCESS);
    }

}
