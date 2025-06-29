package com.gitittogether.skillForge.server.user.dto.request.skill;

import jakarta.validation.constraints.NotBlank;
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
    private String name;
    private String description;
    private String category;
    private String level;
}