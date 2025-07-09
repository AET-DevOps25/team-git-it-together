package com.gitittogether.skillForge.server.course.dto.request.course;

import java.util.List;

/** Payload forwarded to the GenAI service */
public record LearningPathRequest(
        String prompt,
        List<String> existingSkills
) {}
