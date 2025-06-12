package com.gitittogether.skillForge.server.user.dto.request.course;

import com.gitittogether.skillForge.server.user.model.course.LessonContent;
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
