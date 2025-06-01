package com.gitittogether.skillForge.server.mapper;

import com.gitittogether.skillForge.server.dto.response.course.EnrolledCourseResponse;
import com.gitittogether.skillForge.server.model.courses.EnrolledCourse;

public class EnrolledCourseMapper {

    public static EnrolledCourseResponse toEnrolledCourseResponse(EnrolledCourse model) {
        if (model == null) return null;
        return EnrolledCourseResponse.builder()
                .course(CourseMapper.toCourseResponse(model.getCourse()))
                .progress(CourseProgressMapper.toCourseProgressResponse(model.getProgress()))
                .build();
    }
}
