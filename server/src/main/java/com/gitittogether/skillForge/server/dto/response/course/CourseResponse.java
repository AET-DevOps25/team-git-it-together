package com.gitittogether.skillForge.server.dto.response.course;

import com.gitittogether.skillForge.server.dto.response.skill.SkillResponse;
import com.gitittogether.skillForge.server.model.course.Level;
import com.gitittogether.skillForge.server.model.utils.Language;
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
    private String id;
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