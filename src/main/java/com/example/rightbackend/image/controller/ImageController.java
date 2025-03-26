package com.example.rightbackend.image.controller;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.global.config.resolver.Login;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.MemberSuccess;
import com.example.rightbackend.image.controller.dto.response.ImageResponse;
import com.example.rightbackend.image.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<SuccessResponse<ImageResponse>> uploadMultiImage(@Login final LoginMember loginMember,
                                                                           @RequestPart final List<MultipartFile> images) {
        imageService.multiUpload(loginMember, images);
        return SuccessResponse.of(MemberSuccess.IMAGE_UPLOAD_SUCCESS);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<SuccessResponse<ImageResponse>> deleteImage(@Login final LoginMember loginMember, @PathVariable String fileName) {
        imageService.deleteImage(loginMember, fileName);
        return SuccessResponse.of(MemberSuccess.IMAGE_DELETE_SUCCESS);
    }

    @PutMapping("/change/{fileName}")
    public ResponseEntity<SuccessResponse<ImageResponse>> updateImage(@Login final LoginMember loginMember, @PathVariable String fileName, @RequestPart MultipartFile newImage) {
        imageService.deleteImage(loginMember, fileName);
        List<MultipartFile> image = List.of(newImage);
        imageService.multiUpload(loginMember, image);
        return SuccessResponse.of(MemberSuccess.IMAGE_CHANGE_SUCCESS);
    }
}