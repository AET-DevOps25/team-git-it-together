package com.gitittogether.skillForge.server.dto.response.course;

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
    private String courseId;
    private String userId;
    private double progress;
    private LocalDateTime enrolledAt;
    private LocalDateTime lastAccessedAt;
    private boolean completed;
    private LocalDateTime completedAt;
}