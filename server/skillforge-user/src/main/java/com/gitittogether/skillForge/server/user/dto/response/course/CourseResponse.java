package com.gitittogether.skillForge.server.user.dto.response.course;

import com.gitittogether.skillForge.server.user.dto.response.skill.SkillResponse;
import com.gitittogether.skillForge.server.user.model.course.Level;
import com.gitittogether.skillForge.server.user.model.utils.Language;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {
    @NotBlank
    private String id;
    @NotBlank
    private String title;
    private String description;
    private String instructor;
    private List<SkillResponse> skills;
    private List<ModuleResponse> modules;
    private int numberOfEnrolledUsers;
    private List<CategoryResponse> categories;
    private Level level;
    private String thumbnailUrl;
    private boolean published;
    private Language language;
    private double rating;
}