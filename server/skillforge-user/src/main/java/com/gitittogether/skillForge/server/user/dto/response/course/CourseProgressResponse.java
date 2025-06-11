package com.gitittogether.skillForge.server.user.dto.response.course;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseProgressResponse {
    @NotBlank
    private String courseId;
    @NotBlank
    private String userId;
    private double progress;
    private LocalDateTime enrolledAt;
    private LocalDateTime lastAccessedAt;
    private boolean completed;
    private LocalDateTime completedAt;
}