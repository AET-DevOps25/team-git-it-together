package com.gitittogether.skillForge.server.dto.request.course;

import com.gitittogether.skillForge.server.model.course.LessonContent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private LessonContent content;

    @Builder.Default
    private int order = 0;
}
