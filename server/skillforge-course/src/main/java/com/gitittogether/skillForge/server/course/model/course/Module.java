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

    public int getNumberOfLessons() {
        return lessons != null ? lessons.size() : 0;
    }

    public void setLessonOrder(int startOrder) {
        if (lessons != null && !lessons.isEmpty()) {
            for (int i = 0; i < lessons.size(); i++) {
                lessons.get(i).setOrder(startOrder + i);
            }
        }
    }
}