package com.gitittogether.skillForge.server.user.dto.request.course;

import com.gitittogether.skillForge.server.user.dto.request.skill.SkillRequest;
import com.gitittogether.skillForge.server.user.model.course.Level;
import com.gitittogether.skillForge.server.user.model.utils.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequest {
    @NotBlank
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String instructor;

    @Builder.Default
    private List<SkillRequest> skills = new ArrayList<>(); // Skills that can be learned in this course

    @Builder.Default
    private List<ModuleRequest> modules = new ArrayList<>(); // Modules in this course

    @Builder.Default
    private List<String> enrolledUserIds = new ArrayList<>(); // Optional on creation

    @Builder.Default
    private Integer numberOfEnrolledUsers = 0;

    @Builder.Default
    private List<CategoryRequest> categories = new ArrayList<>(); // Course categories

    @Builder.Default
    private Level level = Level.BEGINNER;

    private String thumbnailUrl;

    @Builder.Default
    private boolean published = false;

    @NotNull
    private Language language;

    @Builder.Default
    private double rating = 0.0;
}