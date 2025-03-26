package com.example.rightbackend.image.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.properties.S3Properties;
import com.example.rightbackend.global.response.error.ImageError;
import com.example.rightbackend.image.controller.dto.S3File;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;
    private final S3Properties s3Properties;

    private static final String SUFFIX = ".png";

    private static final String PNG_FILE_EXTENSION = "png";
    private static final String JPEG_FILE_EXTENSION = "jpeg";
    private static final String GIF_FILE_EXTENSION = "gif";
    private static final String JPG_FILE_EXTENSION = "jpg";
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;


    public S3Uploader(final AmazonS3Client amazonS3Client, final S3Properties s3Properties) {
        this.amazonS3Client = amazonS3Client;
        this.s3Properties = s3Properties;
    }

    public List<S3File> multiUpload(List<MultipartFile> files) {
        multiCheckFileSize(files);
        return files.stream().map(this::uploadS3).toList();
    }
    private S3File uploadS3(MultipartFile file) {
        checkFileFormat(file);
        singleCheckFileSize(file);

        String uuidName = UUID.randomUUID() + SUFFIX;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(MediaType.IMAGE_PNG_VALUE);
        metadata.setContentLength(file.getSize());
        try {
            amazonS3Client.putObject(s3Properties.getBucket(),
                    uuidName,
                    file.getInputStream(),
                    metadata);
        } catch (IOException e) {
            throw new RestApiException(ImageError.IMAGE_IO_ERROR);
        }
        return new S3File(uuidName, amazonS3Client.getUrl(s3Properties.getBucket(), uuidName).toString());
    }

    private void checkFileFormat(MultipartFile file) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if(!extension.equalsIgnoreCase(PNG_FILE_EXTENSION) &&
                !extension.equalsIgnoreCase(JPEG_FILE_EXTENSION) &&
                !extension.equalsIgnoreCase(GIF_FILE_EXTENSION) &&
                !extension.equalsIgnoreCase(JPG_FILE_EXTENSION)) {
            throw new RestApiException(ImageError.IMAGE_FORMAT_ERROR);
        }
    }

    private void singleCheckFileSize(MultipartFile file) {
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new RestApiException(ImageError.LIMIT_IMAGE_SIZE_ERROR);
        }
    }

    private void multiCheckFileSize(List<MultipartFile> files) {
        if (files.size() > MAX_IMAGE_SIZE) {
            throw new RestApiException(ImageError.LIMIT_IMAGE_SIZE_ERROR);
        }
    }

    public boolean delete(String fileName) {
        amazonS3Client.deleteObject(s3Properties.getBucket(), fileName);
        return true;
    }
}