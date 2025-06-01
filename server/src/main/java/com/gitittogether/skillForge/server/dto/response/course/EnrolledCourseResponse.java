package com.gitittogether.skillForge.server.dto.response.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrolledCourseResponse {
    private CourseResponse course;
    private CourseProgressResponse progress;
}