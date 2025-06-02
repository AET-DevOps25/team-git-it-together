package com.gitittogether.skillForge.server.dto.request.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ModuleRequest {

    @NotBlank
    private String title;

    @NotNull
    private String description; // Optional

    @NotBlank
    private String courseId; // Parent course's ID (if required for updates/associations)

    @Builder.Default
    @NotNull
    private List<LessonRequest> lessons = new ArrayList<>(); // Lessons in this module

    @Builder.Default
    @NotNull
    private int order = 0; // Position in the course
}
