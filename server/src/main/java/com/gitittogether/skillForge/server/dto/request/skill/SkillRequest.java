package com.gitittogether.skillForge.server.dto.request.skill;

import com.gitittogether.skillForge.server.dto.request.course.CategoryRequest;
import com.gitittogether.skillForge.server.model.course.Level;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRequest {
    @NonNull
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private CategoryRequest category;
    @NotBlank
    private String iconUrl;

    @NotNull
    private Level level;
}