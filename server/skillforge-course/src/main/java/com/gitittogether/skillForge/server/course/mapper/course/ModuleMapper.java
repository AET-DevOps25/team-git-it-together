package com.gitittogether.skillForge.server.course.mapper.course;

import com.gitittogether.skillForge.server.course.dto.request.course.ModuleRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.ModuleResponse;
import com.gitittogether.skillForge.server.course.model.course.Module;

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
                                .map(LessonMapper::toLessonResponse)
                                .collect(Collectors.toList()))
                .order(model.getOrder())
                .build();
    }

    public static ModuleRequest toModuleRequest(Module model) {
        if (model == null) return null;
        return ModuleRequest.builder()
                .title(model.getTitle())
                .description(model.getDescription())
                .courseId(model.getCourseId())
                .lessons(model.getLessons() == null ? null :
                        model.getLessons().stream()
                                .map(LessonMapper::toLessonRequest)
                                .collect(Collectors.toList()))
                .order(model.getOrder())
                .build();
    }

    public static Module requestToModule(ModuleRequest request) {
        if (request == null) return null;
        return Module.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .courseId(request.getCourseId())
                .lessons(request.getLessons() == null ? null :
                        request.getLessons().stream()
                                .map(LessonMapper::requestToLesson)
                                .collect(Collectors.toList()))
                .order(request.getOrder())
                .build();
    }

    public static Module responseToModule(ModuleResponse response) {
        if (response == null) return null;
        return Module.builder()
                .title(response.getTitle())
                .description(response.getDescription())
                .courseId(response.getCourseId())
                .lessons(response.getLessons() == null ? null :
                        response.getLessons().stream()
                                .map(LessonMapper::respnseToLesson)
                                .collect(Collectors.toList()))
                .order(response.getOrder())
                .build();
    }

}
