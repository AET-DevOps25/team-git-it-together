package com.gitittogether.skillForge.server.dto.request;

import com.gitittogether.skillForge.server.model.courses.LessonContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
