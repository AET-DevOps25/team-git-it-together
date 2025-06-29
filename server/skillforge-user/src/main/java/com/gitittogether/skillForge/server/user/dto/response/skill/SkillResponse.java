package com.gitittogether.skillForge.server.user.dto.response.skill;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillResponse {
    @NotBlank
    private String id;
    @NotBlank
    private String name;
    private String description;
    private String category;
    private String difficultyLevel;
}