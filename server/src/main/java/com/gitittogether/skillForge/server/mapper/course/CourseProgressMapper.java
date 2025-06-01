package com.gitittogether.skillForge.server.mapper;

import com.gitittogether.skillForge.server.dto.response.course.CourseProgressResponse;
import com.gitittogether.skillForge.server.model.courses.CourseProgress;

public class CourseProgressMapper {

    public static CourseProgressResponse toCourseProgressResponse(CourseProgress model) {
        if (model == null) return null;
        return CourseProgressResponse.builder()
                .courseId(model.getCourseId())
                .userId(model.getUserId())
                .progress(model.getProgress())
                .enrolledAt(model.getEnrolledAt())
                .lastAccessedAt(model.getLastAccessedAt())
                .completed(model.isCompleted())
                .completedAt(model.getCompletedAt())
                .build();
    }

}
