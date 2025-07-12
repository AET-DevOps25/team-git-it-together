package com.gitittogether.skillForge.server.course.dto.request.course;

import com.gitittogether.skillForge.server.course.model.utils.Language;
import com.gitittogether.skillForge.server.course.model.utils.Level;
import jakarta.validation.Valid;
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
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String instructor;

    @Builder.Default
    private List<String> skills = new ArrayList<>(); // Skills that can be learned in this course

    @Builder.Default
    private List<ModuleRequest> modules = new ArrayList<>(); // Modules in this course

    @Valid
    @Builder.Default
    private List<EnrolledUserInfoRequest> enrolledUsers = new ArrayList<>(); // Optional on creation

    @Builder.Default
    private Integer numberOfEnrolledUsers = 0;

    @Builder.Default
    private List<String> categories = new ArrayList<>(); // Course categories

    @Builder.Default
    private Level level = Level.BEGINNER;

    private String thumbnailUrl;

    @Builder.Default
    private Boolean published = true;

    @Builder.Default
    private Boolean isPublic = true;

    @NotNull
    @Builder.Default
    private Language language = Language.EN; // Default language is English

    @Builder.Default
    private double rating = 0.0;

}