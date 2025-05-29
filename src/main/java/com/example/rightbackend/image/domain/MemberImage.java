package com.example.rightbackend.image.domain;

import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.image.controller.dto.S3File;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class MemberImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberImageId")
    private Long id;

    @Column
    String name;

    @Column
    String url;

    @Column
    private Integer imageIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    protected MemberImage() {
    }

    public static MemberImage from(final S3File imageFile) {
        MemberImage memberImage = new MemberImage();
        memberImage.name = imageFile.fileName();
        memberImage.url = imageFile.fileUrl();
        return memberImage;
    }

    public static MemberImage of(String fileName, String fileUrl) {
        MemberImage memberImage = new MemberImage();
        memberImage.name = fileName;
        memberImage.url = fileUrl;
        return memberImage;
    }
}