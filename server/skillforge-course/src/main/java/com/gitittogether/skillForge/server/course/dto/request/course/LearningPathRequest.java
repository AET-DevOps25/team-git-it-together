package com.gitittogether.skillForge.server.course.dto.request.course;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Payload forwarded to the GenAI service
 */
public record LearningPathRequest(
        @NotBlank
        String prompt,
        List<String> existingSkills
) {
}
