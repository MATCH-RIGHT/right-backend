package com.example.rightbackend.uploader.contrroller.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UploadImageRequest(List<MultipartFile> images) {
}