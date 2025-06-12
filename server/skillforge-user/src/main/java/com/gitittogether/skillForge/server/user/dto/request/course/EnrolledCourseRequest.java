package com.gitittogether.skillForge.server.user.dto.request.course;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrolledCourseRequest {
    @NotBlank
    private CourseRequest course;

    @NotBlank
    private CourseProgressRequest progress;
}
