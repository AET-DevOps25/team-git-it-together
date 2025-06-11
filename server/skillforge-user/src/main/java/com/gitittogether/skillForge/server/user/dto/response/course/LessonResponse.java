package com.gitittogether.skillForge.server.user.dto.response.course;

import com.gitittogether.skillForge.server.user.model.course.LessonContent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponse {
    @NotBlank
    private String title;
    private String description;
    @NotBlank
    private LessonContent content;
    @NotBlank
    private int order;
}