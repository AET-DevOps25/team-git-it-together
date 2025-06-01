package com.gitittogether.skillForge.server.dto.response.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleResponse {
    private String title;
    private String description;
    private String courseId;
    private List<LessonResponse> lessons;
    private int order;
}