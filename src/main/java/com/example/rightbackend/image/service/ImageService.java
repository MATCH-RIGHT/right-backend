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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {
    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;
    private final MemberImageRepository memberImageRepository;

    public ImageService(MemberRepository memberRepository, S3Uploader s3Uploader, MemberImageRepository memberImageRepository) {
        this.memberRepository = memberRepository;
        this.s3Uploader = s3Uploader;
        this.memberImageRepository = memberImageRepository;
    }

    @Transactional
    public void multiUpload(final LoginMember loginMember, final List<MultipartFile> images) {
        Member member = getMember(loginMember);

        List<MemberImage> memberImageEntities = uploadS3(images, member);
        member.setMemberImage(memberImageEntities);
        memberRepository.save(member);
    }

    private List<MemberImage> uploadS3(List<MultipartFile> images, Member member) {
        List<MemberImage> memberImagesEntities = new ArrayList<>();
        List<S3File> s3Files = s3Uploader.multiUpload(images);

        for(S3File image: s3Files) {
            MemberImage memberImage = MemberImage.from(image);
            memberImage.setMember(member);
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
                        image.getUrl()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteImage(final LoginMember loginMember, final String fileName) {
        Member member = getMember(loginMember);
        MemberImage image = memberImageRepository.findByName(fileName)
                .orElseThrow(() -> new RestApiException(ImageError.IMAGE_NOT_FOUND));
        s3Uploader.delete(image.getName());
        memberImageRepository.delete(image);
    }

    private Member getMember(LoginMember loginMember) {
        return memberRepository.findById(loginMember.memberId()).orElseThrow(() -> new RestApiException(MemberError.NULL_MEMBER));
    }
}