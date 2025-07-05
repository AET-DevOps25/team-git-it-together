package com.gitittogether.skillForge.server.course.mapper.course;

import com.gitittogether.skillForge.server.course.dto.request.course.LessonRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.LessonResponse;
import com.gitittogether.skillForge.server.course.model.course.Lesson;

public class LessonMapper {
    public static LessonResponse toLessonResponse(Lesson model) {
        if (model == null) return null;
        return LessonResponse.builder()
                .title(model.getTitle())
                .description(model.getDescription())
                .content(model.getContent())
                .order(model.getOrder())
                .build();
    }

    public static LessonRequest toLessonRequest(Lesson model) {
        if (model == null) return null;
        return LessonRequest.builder()
                .title(model.getTitle())
                .description(model.getDescription())
                .content(model.getContent())
                .order(model.getOrder())
                .build();
    }

    public static Lesson requestToLesson(LessonRequest request) {
        if (request == null) return null;
        return Lesson.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .content(request.getContent())
                .order(request.getOrder())
                .build();
    }

    public static Lesson respnseToLesson(LessonResponse response) {
        if (response == null) return null;
        return Lesson.builder()
                .title(response.getTitle())
                .description(response.getDescription())
                .content(response.getContent())
                .order(response.getOrder())
                .build();
    }
}
