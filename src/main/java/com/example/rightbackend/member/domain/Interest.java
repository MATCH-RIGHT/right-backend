package com.example.rightbackend.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interestName", unique = true)
    private String name;
    
    @Column(name = "icon")
    private String icon;

    @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL)
    private List<MemberProfileToInterest> memberProfileToInterests = new ArrayList<>();

    protected Interest() {
    }

    public static Interest of(String name) {
        Interest interest = new Interest();
        interest.name = name;
        return interest;
    }
    
    public static Interest of(String name, String icon) {
        Interest interest = new Interest();
        interest.name = name;
        interest.icon = icon;
        return interest;
    }
}