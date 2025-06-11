package com.gitittogether.skillForge.server.user.mapper.course;

import com.gitittogether.skillForge.server.user.dto.request.course.EnrolledCourseRequest;
import com.gitittogether.skillForge.server.user.dto.response.course.EnrolledCourseResponse;
import com.gitittogether.skillForge.server.user.model.course.EnrolledCourse;

public class EnrolledCourseMapper {

    public static EnrolledCourseResponse toEnrolledCourseResponse(EnrolledCourse model) {
        if (model == null) return null;
        return EnrolledCourseResponse.builder()
                .course(CourseMapper.toCourseResponse(model.getCourse()))
                .progress(CourseProgressMapper.toCourseProgressResponse(model.getProgress()))
                .build();
    }

    public static EnrolledCourseRequest toEnrolledCourseRequest(EnrolledCourse model) {
        if (model == null) return null;
        return EnrolledCourseRequest.builder()
                .course(CourseMapper.toCourseRequest(model.getCourse()))
                .progress(CourseProgressMapper.toCourseProgressRequest(model.getProgress()))
                .build();
    }

    public static EnrolledCourse requestToEnrolledCourse(EnrolledCourseRequest request) {
        if (request == null) return null;
        return EnrolledCourse.builder()
                .course(CourseMapper.requestToCourse(request.getCourse()))
                .progress(CourseProgressMapper.requestToCourseProgress(request.getProgress()))
                .build();
    }

    public static EnrolledCourse responseToEnrolledCourse(EnrolledCourseResponse response) {
        if (response == null) return null;
        return EnrolledCourse.builder()
                .course(CourseMapper.responseToCourse(response.getCourse()))
                .progress(CourseProgressMapper.responseToCourseProgress(response.getProgress()))
                .build();
    }
}
