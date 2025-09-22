package com.example.rightbackend.image.controller.dto.request;

import java.util.List;

public record ImageReorderRequest(List<Long> imageIds) {
}