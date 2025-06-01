package com.gitittogether.skillForge.server.mapper;

import com.gitittogether.skillForge.server.dto.response.course.ModuleResponse;
import com.gitittogether.skillForge.server.model.courses.Module;

import java.util.stream.Collectors;

public class ModuleMapper {

    public static ModuleResponse toModuleResponse(Module model) {
        if (model == null) return null;
        return ModuleResponse.builder()
                .title(model.getTitle())
                .description(model.getDescription())
                .courseId(model.getCourseId())
                .lessons(model.getLessons() == null ? null :
                        model.getLessons().stream()
                                .map(LessonMapper::toResponse)
                                .collect(Collectors.toList()))
                .order(model.getOrder())
                .build();
    }

}
