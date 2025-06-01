package com.gitittogether.skillForge.server.dto.response.skill;


import com.gitittogether.skillForge.server.dto.response.course.CategoryResponse;
import com.gitittogether.skillForge.server.model.course.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillResponse {
    private String id;
    private String name;
    private String description;
    private CategoryResponse category;
    private Level difficultyLevel;
}