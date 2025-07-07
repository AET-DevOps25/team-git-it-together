package com.gitittogether.skillForge.server.course.dto.response.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    @Builder.Default
    private int currentLesson = 0;
    @Builder.Default
    private int totalNumberOfLessons = 0;
} 