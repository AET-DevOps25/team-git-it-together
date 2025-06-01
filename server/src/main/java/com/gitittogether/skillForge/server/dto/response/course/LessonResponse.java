package com.gitittogether.skillForge.server.dto.response.course;

import com.gitittogether.skillForge.server.model.course.LessonContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponse {
    private String title;
    private String description;
    private LessonContent content;
    private int order;
}