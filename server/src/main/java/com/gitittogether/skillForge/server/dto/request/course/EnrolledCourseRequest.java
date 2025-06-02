package com.gitittogether.skillForge.server.dto.request.course;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrolledCourseRequest {
    @NotNull
    private CourseRequest course;

    @NotNull
    private CourseProgressRequest progress;
}
