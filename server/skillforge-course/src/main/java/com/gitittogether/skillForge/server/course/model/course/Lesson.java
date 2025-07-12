package com.gitittogether.skillForge.server.course.model.course;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {
    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private LessonContent content;

    private String thumbnail;

    @Builder.Default
    private int order = 0; // The position of this lesson within its module
}