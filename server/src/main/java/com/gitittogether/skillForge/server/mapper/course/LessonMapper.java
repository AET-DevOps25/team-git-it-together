package com.gitittogether.skillForge.server.mapper;

import com.gitittogether.skillForge.server.dto.response.course.LessonResponse;
import com.gitittogether.skillForge.server.model.courses.Lesson;

public class LessonMapper {
    public static LessonResponse toResponse(Lesson model) {
        if (model == null) return null;
        return LessonResponse.builder()
                .title(model.getTitle())
                .description(model.getDescription())
                .content(model.getContent())
                .order(model.getOrder())
                .build();
    }
}
