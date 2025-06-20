package com.gitittogether.skillForge.server.user.model.course;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseProgress {
    @NonNull
    private String courseId;
    @NonNull
    private String userId; // User who is enrolled in the course
    @Builder.Default
    private double progress = 0.0; // Percentage of course completed (0.0 to 100.0)
    @Builder.Default
    private LocalDateTime enrolledAt = LocalDateTime.now(); // When the user enrolled in the course
    @Builder.Default
    private LocalDateTime lastAccessedAt = LocalDateTime.now(); // Last time the user accessed the course
    @Builder.Default
    private boolean completed = false; // Whether the course has been completed
    @Builder.Default
    private LocalDateTime completedAt = null; // When the course was completed, null if not completed
}