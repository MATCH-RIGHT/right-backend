package com.example.rightbackend.global.config.loader;

import com.example.rightbackend.member.domain.Interest;
import com.example.rightbackend.member.domain.repository.InterestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;

@Configuration
@Component
public class InterestDataLoader {

    private final InterestRepository interestRepository;
    
    public InterestDataLoader(InterestRepository interestRepository) {
        this.interestRepository = interestRepository;
    }

    @Bean
    public CommandLineRunner initInterestData() {
        return args -> {
            loadInterestData();
        };
    }
    
    public void loadInterestData() {
        HashMap<String, String> interestIcons = new HashMap<>();
        interestIcons.put("독서", "📚");
        interestIcons.put("강아지", "🐶");
        interestIcons.put("여행", "✈️");
        interestIcons.put("사진촬영", "📸");
        interestIcons.put("러닝", "🏃");
        interestIcons.put("그림 그리기", "🎨");
        interestIcons.put("고양이", "🐈");
        interestIcons.put("운동하기", "💪");
        interestIcons.put("집콕", "🛌");
        interestIcons.put("카페 가기", "☕");
        interestIcons.put("음악 듣기", "🎧");
        interestIcons.put("패션", "🧤");
        interestIcons.put("뮤지컬 관람", "🧙");
        interestIcons.put("악기 연주", "🎹");
        interestIcons.put("드라마", "📺");
        interestIcons.put("게임", "🎮");
        interestIcons.put("요리", "🥘");
        interestIcons.put("영화", "🎥");
        interestIcons.put("전시회", "🎪");
        interestIcons.put("스포츠", "⚽");
        interestIcons.put("재테크", "💵");
        interestIcons.put("쇼핑하기", "🛒");
        interestIcons.put("애니메이션", "💭");
        interestIcons.put("공부", "📖");
        interestIcons.put("파티, 모임", "🥳");
        interestIcons.put("술", "🥃");
        interestIcons.put("클라이밍", "🧗");
        interestIcons.put("헬스", "🏋️");
        interestIcons.put("요가", "🧘");
        interestIcons.put("골프", "⛳");
        interestIcons.put("서핑", "🏄");
        interestIcons.put("가벼운 산책", "🚶");
        interestIcons.put("노래부르기", "🎤");
        interestIcons.put("맛집", "🤤");
        interestIcons.put("드라이브", "🚘");
        interestIcons.put("등산", "🥾");
        interestIcons.put("연애", "💌");
        interestIcons.put("친구만들기", "😎");
        interestIcons.put("취업", "💼");
        interestIcons.put("베이킹", "🥐");

        List<String> interestNames = new ArrayList<>(interestIcons.keySet());
        
        interestNames = interestNames.stream().distinct().toList();

        for (String name : interestNames) {
            Optional<Interest> existingInterest = interestRepository.findByName(name);
            if (existingInterest.isEmpty()) {
                Interest interest = Interest.of(name, interestIcons.get(name));
                interestRepository.save(interest);
            } else {
                Interest interest = existingInterest.get();
                if (interest.getIcon() == null) {
                    interest.setIcon(interestIcons.get(name));
                    interestRepository.save(interest);
                }
            }
        }
    }
}