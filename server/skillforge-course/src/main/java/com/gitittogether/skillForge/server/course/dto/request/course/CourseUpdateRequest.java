package com.gitittogether.skillForge.server.course.dto.request.course;

import com.gitittogether.skillForge.server.course.model.utils.Language;
import com.gitittogether.skillForge.server.course.model.utils.Level;
import jakarta.validation.Valid;
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
public class CourseUpdateRequest {
    private String id;
    private String title;
    private String description;
    private String instructor;

    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @Builder.Default
    private List<ModuleRequest> modules = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<EnrolledUserInfoRequest> enrolledUsers = new ArrayList<>();

    @Builder.Default
    private Integer numberOfEnrolledUsers = 0;

    @Builder.Default
    private List<String> categories = new ArrayList<>();

    @Builder.Default
    private Level level = Level.BEGINNER;

    private String thumbnailUrl;

    @Builder.Default
    private Boolean published = true;

    @Builder.Default
    private Boolean isPublic = true;

    @Builder.Default
    private Language language = Language.EN;

    @Builder.Default
    private double rating = 0.0;
} 