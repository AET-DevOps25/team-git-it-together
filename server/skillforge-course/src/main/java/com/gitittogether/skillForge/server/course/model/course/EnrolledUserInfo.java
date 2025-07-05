package com.gitittogether.skillForge.server.course.model.course;

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
public class EnrolledUserInfo {
    private String userId;
    @Builder.Default
    private float progress = 0.0f;
    @Builder.Default
    private List<String> skills = new ArrayList<>();
} 