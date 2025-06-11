package com.gitittogether.skillForge.server.user.model.course;

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