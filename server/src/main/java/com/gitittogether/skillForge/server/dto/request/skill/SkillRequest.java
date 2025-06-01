package com.gitittogether.skillForge.server.dto.request;

import com.gitittogether.skillForge.server.model.courses.Level;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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