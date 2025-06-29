package com.gitittogether.skillForge.server.course.mapper.course;

import com.gitittogether.skillForge.server.course.dto.request.course.CourseProgressRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseProgressResponse;
import com.gitittogether.skillForge.server.course.model.course.CourseProgress;

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

    public static CourseProgressRequest toCourseProgressRequest(CourseProgress model) {
        if (model == null) return null;
        return CourseProgressRequest.builder()
                .courseId(model.getCourseId())
                .userId(model.getUserId())
                .progress(model.getProgress())
                .enrolledAt(model.getEnrolledAt())
                .lastAccessedAt(model.getLastAccessedAt())
                .completed(model.isCompleted())
                .completedAt(model.getCompletedAt())
                .build();
    }

    public static CourseProgress responseToCourseProgress(CourseProgressResponse response) {
        if (response == null) return null;
        return CourseProgress.builder()
                .courseId(response.getCourseId())
                .userId(response.getUserId())
                .progress(response.getProgress())
                .enrolledAt(response.getEnrolledAt())
                .lastAccessedAt(response.getLastAccessedAt())
                .completed(response.isCompleted())
                .completedAt(response.getCompletedAt())
                .build();
    }

    public static CourseProgress requestToCourseProgress(CourseProgressRequest request) {
        if (request == null) return null;
        return CourseProgress.builder()
                .courseId(request.getCourseId())
                .userId(request.getUserId())
                .progress(request.getProgress())
                .enrolledAt(request.getEnrolledAt())
                .lastAccessedAt(request.getLastAccessedAt())
                .completed(request.isCompleted())
                .completedAt(request.getCompletedAt())
                .build();
    }

}
