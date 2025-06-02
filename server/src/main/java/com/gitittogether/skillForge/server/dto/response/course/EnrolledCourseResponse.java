package com.gitittogether.skillForge.server.dto.response.course;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrolledCourseResponse {
    @NotBlank
    private CourseResponse course;
    @NotBlank
    private CourseProgressResponse progress;
}