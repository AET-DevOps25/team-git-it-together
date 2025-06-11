package com.gitittogether.skillForge.server.user.dto.response.skill;


import com.gitittogether.skillForge.server.user.dto.response.course.CategoryResponse;
import com.gitittogether.skillForge.server.user.model.course.Level;
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
    private CategoryResponse category;
    private Level difficultyLevel;
}