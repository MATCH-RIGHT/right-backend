package com.example.rightbackend.rekognition.service;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.domain.Member;
import com.example.rightbackend.auth.domain.repository.MemberRepository;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.MemberError;
import com.example.rightbackend.member.domain.MemberProfile;
import com.example.rightbackend.member.service.TextEncoder;
import com.example.rightbackend.rekognition.controller.dto.detection.FaceDetail;
import com.example.rightbackend.rekognition.controller.dto.detection.attribute.AgeRange;
import com.example.rightbackend.rekognition.controller.dto.detection.attribute.BoundingBox;
import com.example.rightbackend.rekognition.controller.dto.detection.attribute.FacialAttribute;
import com.example.rightbackend.rekognition.controller.dto.detection.attribute.Landmark;
import com.example.rightbackend.rekognition.domain.Feature.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class S3FaceAnalysis {

    private final MemberRepository memberRepository;

    public S3FaceAnalysis(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<String> analyzeFaceFeatures(final List<FaceDetail> lFaceDetail, final MemberProfile memberProfile) {
        FaceDetail faceDetail = lFaceDetail.get(0);

        FaceShape faceShape = determineFaceShape(faceDetail);
        NoseShape noseShape = determineNoseShape(faceDetail);
        EyeSize eyeSize = determineEyeSize(faceDetail);
        EyeType eyeType = determineEyeType(faceDetail);
        LipShape lipShape = determineLipShape(faceDetail);
        SkinTone skinTone = determineSkinTone(faceDetail);
        ForeHead forehead = determineForehead(faceDetail);
        JawShape jawline = determineJawline(faceDetail);
        Age ageRange = determineAgeRange(faceDetail, memberProfile);
        Glass glasses = determineGlasses(faceDetail);
        Beard beard = determineBeard(faceDetail);
        AnimalLook animalLook = determineAnimalLook(faceDetail);

        return List.of(
                faceShape != null ? faceShape.getName() : null,
                noseShape != null ? noseShape.getName() : null,
                eyeSize != null ? eyeSize.getName() : null,
                eyeType != null ? eyeType.getName() : null,
                lipShape != null ? lipShape.getName() : null,
                skinTone != null ? skinTone.getName() : null,
                forehead != null ? forehead.getName() : null,
                jawline != null ? jawline.getName() : null,
                ageRange != null ? ageRange.getName() : null,
                glasses != null ? glasses.getName() : null,
                beard != null ? beard.getName() : null,
                animalLook != null ? animalLook.getName() : null
        );
    }

    private FaceShape determineFaceShape(FaceDetail faceDetail) {
        if (faceDetail == null || faceDetail.boundingBox() == null)
            return null;

        BoundingBox boundingBox = faceDetail.boundingBox();
        float width = boundingBox.width();
        float height = boundingBox.height();

        if (width < 0.05 || height < 0.05)
            return null;

        float ratio = width / height;

        FaceShape shape;
        if (ratio < 0.65) shape = FaceShape.LONG;
        else if (ratio < 0.78) shape = FaceShape.OVAL;
        else if (ratio < 0.92) shape = FaceShape.ROUND;
        else if (ratio < 0.98) shape = FaceShape.TRIANGLE;
        else shape = FaceShape.ANGULAR;

        return shape;
    }

    private NoseShape determineNoseShape(FaceDetail faceDetail) {
        Landmark nose = faceDetail.landmarks().stream()
                .filter(l -> l.type().equals("nose"))
                .findFirst()
                .orElse(null);

        if (nose == null)
            return null;

        NoseShape noseShape;
        float noseY = nose.y();
        if (noseY < 0.28) noseShape = NoseShape.SMART;
        else if (noseY < 0.48) noseShape = NoseShape.BALANCE;
        else noseShape = NoseShape.LOW;
        return noseShape;
    }

    private EyeSize determineEyeSize(FaceDetail faceDetail) {
        List<Landmark> landmarks = faceDetail.landmarks();
        float eyeSize = calculateDistance(
                findLandmark(landmarks, "leftEyeLeft"),
                findLandmark(landmarks, "leftEyeRight")
        );

        EyeSize size;
        if (eyeSize < 0.1) size = EyeSize.SMALL;
        else size = EyeSize.BIG;

        return size;
    }

    private EyeType determineEyeType(FaceDetail faceDetail) {
        List<Landmark> landmarks = faceDetail.landmarks();

        Landmark leftEyeLeft = findLandmark(landmarks, "leftEyeLeft");
        Landmark leftEyeRight = findLandmark(landmarks, "leftEyeRight");
        Landmark leftEyeTop = findLandmark(landmarks, "leftEyeTop");
        Landmark leftEyeBottom = findLandmark(landmarks, "leftEyeBottom");

        Landmark rightEyeLeft = findLandmark(landmarks, "rightEyeLeft");
        Landmark rightEyeRight = findLandmark(landmarks, "rightEyeRight");
        Landmark rightEyeTop = findLandmark(landmarks, "rightEyeTop");
        Landmark rightEyeBottom = findLandmark(landmarks, "rightEyeBottom");

        if (leftEyeLeft == null || leftEyeRight == null || rightEyeLeft == null || rightEyeRight == null)
            return null;

        float leftEyeTilt = calculateAngle(leftEyeLeft, leftEyeRight, null);
        float rightEyeTilt = calculateAngle(rightEyeLeft, rightEyeRight, null);

        float avgEyeTilt = (leftEyeTilt + rightEyeTilt) / 2;

        float leftEyeHeight = calculateDistance(leftEyeTop, leftEyeBottom);
        float leftEyeWidth = calculateDistance(leftEyeLeft, leftEyeRight);
        float rightEyeHeight = calculateDistance(rightEyeTop, rightEyeBottom);
        float rightEyeWidth = calculateDistance(rightEyeLeft, rightEyeRight);

        float avgEyeRatio = ((leftEyeHeight / leftEyeWidth) + (rightEyeHeight / rightEyeWidth)) / 2;

        EyeType eyeType;
        if (avgEyeTilt > 7) eyeType = EyeType.UPTURNED;
        else if (avgEyeTilt < -3) eyeType = EyeType.DOWNTURNED;
        else if (avgEyeRatio > 0.5) eyeType = EyeType.ROUND;
        else eyeType = EyeType.ALMOND;
        return eyeType;
    }

    private LipShape determineLipShape(FaceDetail faceDetail) {
        FacialAttribute mouthOpen = faceDetail.mouthOpen();

        LipShape lipShape;
        if (mouthOpen != null && mouthOpen.confidence() > 75)
            lipShape = LipShape.THICK;
        else
            lipShape = LipShape.THIN;
        return lipShape;
    }

    private SkinTone determineSkinTone(FaceDetail faceDetail) {
        float brightness = faceDetail.quality().brightness();

        SkinTone skinTone;
        if (brightness < 0.3) skinTone = SkinTone.DARK;
        else skinTone = SkinTone.BRIGHT;
        return skinTone;
    }

    private ForeHead determineForehead(FaceDetail faceDetail) {
        List<Landmark> landmarks = faceDetail.landmarks();
        float foreheadWidth = calculateDistance(
            findLandmark(landmarks, "leftEyeBrowLeft"),
            findLandmark(landmarks, "rightEyeBrowRight")
        );

        ForeHead foreHead;
        if (foreheadWidth < 0.4) foreHead = ForeHead.NARROW;
        else foreHead = ForeHead.BROAD;
        return foreHead;
    }

    private JawShape determineJawline(FaceDetail faceDetail) {
        List<Landmark> landmarks = faceDetail.landmarks();
        float jawAngle = calculateAngle(
            findLandmark(landmarks, "leftJaw"),
            findLandmark(landmarks, "chinBottom"),
            findLandmark(landmarks, "rightJaw")
        );

        JawShape jawShape;
        if (jawAngle < 105) jawShape = JawShape.SQUARE;
        else if (jawAngle < 145) jawShape = JawShape.ROUND;
        else jawShape = JawShape.SHARP;
        return jawShape;
    }

    private Age determineAgeRange(FaceDetail faceDetail, MemberProfile memberProfile) {
        AgeRange ageRange = faceDetail.ageRange();
        if (ageRange == null)
            return null;
        int low = ageRange.low();
        int high = ageRange.high();
        int avgEstimatedAge = (low + high) / 2;

        int nowAge = calculateAgeFromBirthday(TextEncoder.decrypt(memberProfile.getBirthday()));
        int ageDifference = avgEstimatedAge - nowAge;

        Age age;
        if(ageDifference > 0) age = Age.ADULT;
        else age = Age.YOUNG;
        return age;
    }

    private int calculateAgeFromBirthday(String birthdayStr) {
        LocalDate birthDate;

        if (birthdayStr.contains("-"))
            birthDate = LocalDate.parse(birthdayStr);
        else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            birthDate = LocalDate.parse(birthdayStr, formatter);
        }

        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    private Glass determineGlasses(FaceDetail faceDetail) {
        FacialAttribute glasses = faceDetail.eyeglasses();
        Glass glassRes;
        if (glasses != null && glasses.value())
            glassRes = Glass.GLASSED;
        else
            glassRes = Glass.NOT;
        return glassRes;
    }

    private Beard determineBeard(FaceDetail faceDetail) {
        FacialAttribute beard = faceDetail.beard();
        Beard beardRes;
        if (beard != null && beard.value())
            beardRes = Beard.BEARDED;
        else
            beardRes = Beard.NOT;
        return beardRes;
    }

    private AnimalLook determineAnimalLook(FaceDetail faceDetail) {
        List<Landmark> landmarks = faceDetail.landmarks();

        String faceShape = String.valueOf(determineFaceShape(faceDetail));
        String eyeType = String.valueOf(determineEyeType(faceDetail));
        String noseShape = String.valueOf(determineNoseShape(faceDetail));
        String eyeSize = String.valueOf(determineEyeSize(faceDetail));

        float eyeDistance = calculateDistance(
                findLandmark(landmarks, "leftEye"),
                findLandmark(landmarks, "rightEye")
        );

        Landmark nose = findLandmark(landmarks, "nose");
        Landmark mouthUp = findLandmark(landmarks, "mouthUp");
        float noseMouthDistance = 0;
        if (nose != null && mouthUp != null) {
            noseMouthDistance = calculateDistance(nose, mouthUp);
        }

        int catScore = 0, dogScore = 0, bearScore = 0, rabbitScore = 0,
                foxScore = 0, quokkaScore = 0, dinoScore = 0, horseScore = 0;

        // 고양이상: 각진 얼굴, 눈꼬리 올라감, 작은 코
        if (faceShape.equals("각진형") || faceShape.equals("역삼각형")) catScore += 2;
        if (eyeType.equals("눈꼬리가 올라간 눈")) catScore += 3;
        if (noseShape.equals("작은 코")) catScore += 2;

        // 강아지상: 둥근 얼굴, 큰 눈, 코와 입 사이 거리 짧음
        if (faceShape.equals("둥근형")) dogScore += 2;
        if (eyeSize.equals("큰")) dogScore += 2;
        if (noseMouthDistance < 0.1) dogScore += 3;

        // 곰돌이상: 둥근 얼굴, 작은 눈, 넓은 코
        if (faceShape.equals("둥근형")) bearScore += 4;
        if (eyeSize.equals("작은")) bearScore += 3;
        if (noseShape.equals("넓은 코")) bearScore += 2;

        // 토끼상: 계란형 얼굴, 큰 눈, 작은 코
        if (faceShape.equals("계란형")) rabbitScore += 2;
        if (eyeSize.equals("큰")) rabbitScore += 2;
        if (noseShape.equals("작은 코")) rabbitScore += 1;

        // 여우상: 역삼각형 얼굴, 눈꼬리 올라감, 뾰족한 턱
        if (faceShape.equals("역삼각형")) foxScore += 2;
        if (eyeType.equals("눈꼬리가 올라간 눈")) foxScore += 2;
        if (determineChinShape(faceDetail).equals("뾰족한")) foxScore += 2;

        // 쿼카상: 둥근 얼굴, 큰 미소, 둥근 코
        if (faceShape.equals("둥근형")) quokkaScore += 2;
        if (faceDetail.smile() != null && faceDetail.smile().value()) quokkaScore += 3;

        // 공룡상: 긴 얼굴, 작은 눈, 큰 코
        if (faceShape.equals("긴 언굴형")) dinoScore += 3;
        if (eyeSize.equals("작은")) dinoScore += 1;
        if (noseShape.equals("큰 코")) dinoScore += 2;

        // 말상: 긴 얼굴, 눈 사이 거리 넓음, 긴 코
        if (faceShape.equals("긴 언굴형")) horseScore += 3;
        if (eyeDistance > 0.4) horseScore += 2;
        if (noseShape.equals("긴 코")) horseScore += 2;

        Map<AnimalLook, Integer> scores = Map.of(
                AnimalLook.CAT, catScore,
                AnimalLook.DOG, dogScore,
                AnimalLook.BEAR, bearScore,
                AnimalLook.RABBIT, rabbitScore,
                AnimalLook.FOX, foxScore,
                AnimalLook.QUOKKA, quokkaScore,
                AnimalLook.DINO, dinoScore,
                AnimalLook.HORSE, horseScore
        );

        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private String determineChinShape(FaceDetail faceDetail) {
        BoundingBox boundingBox = faceDetail.boundingBox();
        float width = boundingBox.width();
        float height = boundingBox.height();
        float ratio = width / height;

        if (ratio < 0.7) return "뾰족한";
        if (ratio < 0.95) return "둥근";
        return "각진";
    }

    private Landmark findLandmark(List<Landmark> landmarks, String type) {
        return landmarks.stream()
                .filter(l -> l.type().equals(type))
                .findFirst()
                .orElse(null);
    }

    private float calculateDistance(Landmark p1, Landmark p2) {
        if (p1 == null || p2 == null) return 0;
        float dx = p1.x() - p2.x();
        float dy = p1.y() - p2.y();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private float calculateAngle(Landmark p1, Landmark p2, Landmark p3) {
        if (p1 == null || p2 == null || p3 == null) return 0;
        float v1x = p1.x() - p2.x();
        float v1y = p1.y() - p2.y();
        float v2x = p3.x() - p2.x();
        float v2y = p3.y() - p2.y();
        float dot = v1x * v2x + v1y * v2y;
        float det = v1x * v2y - v1y * v2x;
        return (float) Math.toDegrees(Math.atan2(det, dot));
    }

    private Member getMember(LoginMember loginMember) {
        return memberRepository.findById(loginMember.memberId()).orElseThrow(() -> new RestApiException(MemberError.NULL_MEMBER));
    }
}