package com.gitittogether.skillForge.server.course.model.course;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrolledCourse {

    @NonNull
    private Course course;

    @NonNull
    private CourseProgress progress;
}