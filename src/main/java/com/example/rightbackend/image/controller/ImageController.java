package com.example.rightbackend.image.controller;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.global.config.resolver.Login;
import com.example.rightbackend.global.response.SuccessResponse;
import com.example.rightbackend.global.response.success.ImageSuccess;
import com.example.rightbackend.image.controller.dto.request.ImageReorderRequest;
import com.example.rightbackend.image.controller.dto.response.ImageListResponse;
import com.example.rightbackend.image.controller.dto.response.ImageResponse;
import com.example.rightbackend.image.service.ImageService;
import com.example.rightbackend.rekognition.controller.dto.response.FaceFeatureListResponse;
import com.example.rightbackend.rekognition.controller.dto.response.FaceFeatureResponse;
import com.example.rightbackend.rekognition.service.RekognitionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    private final ImageService imageService;
    private final RekognitionService rekognitionService;

    public ImageController(ImageService imageService, RekognitionService rekognitionService) {
        this.imageService = imageService;
        this.rekognitionService = rekognitionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<SuccessResponse<ImageResponse>> uploadMultiImage(@Login final LoginMember loginMember,
                                                                           @RequestPart final List<MultipartFile> image) {
        imageService.multiUpload(loginMember, image);
        return SuccessResponse.of(ImageSuccess.IMAGE_UPLOAD_SUCCESS);
    }

    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<SuccessResponse<ImageResponse>> deleteImage(@Login final LoginMember loginMember, @PathVariable Long imageId) {
        imageService.deleteImage(loginMember, imageId);
        return SuccessResponse.of(ImageSuccess.IMAGE_DELETE_SUCCESS);
    }

    @GetMapping("/get-profile-images")
    public ResponseEntity<SuccessResponse<List<ImageListResponse>>> getAllImages(@Login LoginMember loginMember) {
        List<ImageListResponse> imageList = imageService.getImageList(loginMember);
        return SuccessResponse.of(ImageSuccess.IMAGE_GET_SUCCESS, imageList);
    }

    @PutMapping("/reorder")
    public ResponseEntity<SuccessResponse<List<ImageListResponse>>> reorderImages(@Login LoginMember loginMember,
                                                                                  @RequestBody ImageReorderRequest request) {
        List<ImageListResponse> imageList = imageService.reorderImages(loginMember, request.imageIds());
        return SuccessResponse.of(ImageSuccess.IMAGE_REORDER_SUCCESS, imageList);
    }

    @GetMapping("/get-all-features")
    public ResponseEntity<SuccessResponse<FaceFeatureListResponse>> getAllFaceFeatures() {
        FaceFeatureListResponse response = imageService.getAllFaceFeatures();
        return SuccessResponse.of(ImageSuccess.ALL_FACE_FEATURES_GET_SUCCESS, response);
    }

    @PostMapping(value = "/upload-feature", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessResponse<ImageResponse>> detectFaces(@Login LoginMember loginMember, @RequestPart("image") MultipartFile imageFile) {
        rekognitionService.detectFaces(loginMember, imageFile);
        return SuccessResponse.of(ImageSuccess.MY_FEATURE_UPLOAD_SUCCESS);
    }

    @GetMapping("/get-my-features")
    public ResponseEntity<FaceFeatureResponse> getFaceFeature(@Login LoginMember loginMember) {
        List<Map<String, Object>> faceAnalysisResponse = rekognitionService.getFaceFeature(loginMember);
        FaceFeatureResponse response = new FaceFeatureResponse(faceAnalysisResponse);
        return SuccessResponse.of(ImageSuccess.MY_FEATURE_GET_SUCCESS, response);
    }
}