package com.gitittogether.skillForge.server.course.dto.response.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrolledUserInfoResponse {
    private String userId;
    @Builder.Default
    private float progress = 0.0f;
    @Builder.Default
    private List<String> skills = new ArrayList<>();
} 