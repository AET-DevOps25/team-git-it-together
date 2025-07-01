package com.gitittogether.skillForge.server.course.model.course;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "enrolled_courses")
public class EnrolledCourse {

    @Id
    private String id;

    @NonNull
    private Course course;

    @NonNull
    private CourseProgress progress;

    @Builder.Default
    private boolean isCompleted = false; // Indicates if the course has been completed by the user
}