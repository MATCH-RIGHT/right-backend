package com.example.rightbackend.member.controller.dto.response;

import com.example.rightbackend.member.domain.constant.Gender;
import com.example.rightbackend.member.domain.constant.BodyType;
import com.example.rightbackend.member.domain.constant.Job;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record ConstantsResponse(
    List<ConstantItem> genders,
    List<ConstantItem> bodyTypes,
    List<ConstantItem> jobs
) {
    public record ConstantItem(int id, String name, String label) {}

    public static ConstantsResponse create() {
        return new ConstantsResponse(
            Arrays.stream(Gender.values())
                .map(g -> new ConstantItem(g.getId(), g.name(), g.getLabel()))
                .collect(Collectors.toList()),
            Arrays.stream(BodyType.values())
                .map(b -> new ConstantItem(b.getId(), b.name(), b.getLabel()))
                .collect(Collectors.toList()),
            Arrays.stream(Job.values())
                .map(j -> new ConstantItem(j.getId(), j.name(), j.getLabel()))
                .collect(Collectors.toList())
        );
    }
}