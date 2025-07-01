package com.gitittogether.skillForge.server.course.model.course;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module {

    @NonNull
    private String title;

    private String description; // Optional description of the module

    @Builder.Default
    private List<Lesson> lessons = new ArrayList<>(); // Lessons in this module

    @Builder.Default
    private int order = 0;
}