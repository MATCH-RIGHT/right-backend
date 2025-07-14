package com.example.rightbackend.image.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.ImageError;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.image.controller.dto.S3File;
import com.example.rightbackend.image.controller.dto.response.ImageListResponse;
import com.example.rightbackend.image.domain.MemberImage;
import com.example.rightbackend.image.domain.repository.MemberImageRepository;
import com.example.rightbackend.rekognition.controller.dto.response.FaceFeatureListResponse;
import com.example.rightbackend.rekognition.domain.FaceFeature;
import com.example.rightbackend.rekognition.domain.repository.FaceFeatureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {
    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;
    private final MemberImageRepository memberImageRepository;
    private final FaceFeatureRepository faceFeatureRepository;


    public ImageService(MemberRepository memberRepository, S3Uploader s3Uploader, MemberImageRepository memberImageRepository, RekognitionClient rekognitionClient, FaceFeatureRepository faceFeatureRepository) {
        this.memberRepository = memberRepository;
        this.s3Uploader = s3Uploader;
        this.memberImageRepository = memberImageRepository;
        this.faceFeatureRepository = faceFeatureRepository;
    }

    @Transactional
    public void multiUpload(final LoginMember loginMember, final List<MultipartFile> images) {
        Member member = getMember(loginMember);

        List<MemberImage> newMemberImageEntities = uploadS3(images, member);
        
        List<MemberImage> existingImages = member.getMemberImage();
        if (existingImages == null) {
            existingImages = new ArrayList<>();
        }
        
        existingImages.addAll(newMemberImageEntities);
        member.setMemberImage(existingImages);
        
        memberRepository.save(member);
    }

    private List<MemberImage> uploadS3(List<MultipartFile> images, Member member) {
        List<MemberImage> memberImagesEntities = new ArrayList<>();
        List<S3File> s3Files = s3Uploader.multiUpload(images);
        
        int lastIndex = 0;
        List<MemberImage> existingImages = member.getMemberImage();
        if (!existingImages.isEmpty()) {
            lastIndex = existingImages.stream()
                    .mapToInt(MemberImage::getImageIndex)
                    .max()
                    .orElse(0);
        }

        int index = lastIndex;
        for(S3File image: s3Files) {
            MemberImage memberImage = MemberImage.from(image);
            memberImage.setMember(member);
            memberImage.setImageIndex(++index);
            memberImagesEntities.add(memberImage);
        }
        return memberImagesEntities;
    }

    @Transactional
    public List<ImageListResponse> getImageList(final LoginMember loginMember) {
        Member member = getMember(loginMember);
        List<MemberImage> memberImages = member.getMemberImage();
        return memberImages.stream()
                .map(image -> new ImageListResponse(
                        image.getId(),
                        image.getName(),
                        image.getUrl(),
                        image.getImageIndex()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteImage(final LoginMember loginMember, final String fileName) {
        MemberImage image = memberImageRepository.findByName(fileName)
                .orElseThrow(() -> new RestApiException(ImageError.IMAGE_NOT_FOUND));
        
        Member member = getMember(loginMember);
        if (!image.getMember().getId().equals(member.getId())) {
            throw new RestApiException(ImageError.UNAUTHORIZED_IMAGE_ACCESS);
        }
        
        s3Uploader.delete(image.getName());
        memberImageRepository.delete(image);
    }

    @Transactional(readOnly = true)
    public FaceFeatureListResponse getAllFaceFeatures() {
        List<FaceFeature> faceFeatures = faceFeatureRepository.findAllByOrderByIdAsc();
        return new FaceFeatureListResponse(faceFeatures);
    }
    
    @Transactional(readOnly = true)
    public List<String> getMemberImageUrls(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RestApiException(MemberError.NULL_MEMBER));
        
        List<MemberImage> memberImages = member.getMemberImage();
        return memberImages.stream()
                .map(MemberImage::getUrl)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ImageListResponse> getMemberImages(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RestApiException(MemberError.NULL_MEMBER));
        
        List<MemberImage> memberImages = member.getMemberImage();
        return memberImages.stream()
                .map(image -> new ImageListResponse(
                        image.getId(),
                        image.getName(),
                        image.getUrl(),
                        image.getImageIndex()))
                .collect(Collectors.toList());
    }

    private Member getMember(LoginMember loginMember) {
        return memberRepository.findById(loginMember.memberId()).orElseThrow(() -> new RestApiException(MemberError.NULL_MEMBER));
    }
}