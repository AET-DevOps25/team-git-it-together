package com.gitittogether.skillForge.server.course.dto.response.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private List<LessonResponse> lessons;
    @NotBlank
    private int order;
}