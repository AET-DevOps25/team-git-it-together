package com.gitittogether.skillForge.server.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrolledCourseRequest {
    @NonNull
    private CourseRequest course;

    @NonNull
    private CourseProgressRequest progress;
}
