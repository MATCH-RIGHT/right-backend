package com.example.rightbackend.uploader.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.uploader.controller.dto.S3File;
import com.example.rightbackend.uploader.domain.MemberImage;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class UploadService {
    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;

    public UploadService(MemberRepository memberRepository, S3Uploader s3Uploader) {
        this.memberRepository = memberRepository;
        this.s3Uploader = s3Uploader;
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

    private Member getMember(LoginMember loginMember) {
        return memberRepository.findById(loginMember.memberId()).orElseThrow(() -> new RestApiException(MemberError.NULL_MEMBER));
    }
}
