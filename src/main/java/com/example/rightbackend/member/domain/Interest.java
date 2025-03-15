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
    @Column(name = "interest_name")
    private String name;

    @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL)
    private List<MemberProfileToInterest> memberProfileToInterests = new ArrayList<>();

    public Interest(String name) {
        this.name = name;
    }

    protected Interest() {
    }

    public static Interest of(String name) {
        Interest interest = new Interest();
        interest.name = name;
        return interest;
    }
}