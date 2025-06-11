package com.gitittogether.skillForge.server.user.dto.request.skill;

import com.gitittogether.skillForge.server.user.dto.request.course.CategoryRequest;
import com.gitittogether.skillForge.server.user.model.course.Level;
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
public class SkillRequest {
    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private CategoryRequest category;

    @NotNull
    private String iconUrl;

    @NotNull
    private Level level;
}