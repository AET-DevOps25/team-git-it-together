package com.gitittogether.skillForge.server.course.dto.response.course;

import com.gitittogether.skillForge.server.course.model.utils.Language;
import com.gitittogether.skillForge.server.course.model.utils.Level;
import com.gitittogether.skillForge.server.course.model.course.EnrolledUserInfo;
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
public class CourseSummaryResponse {
    @NotBlank
    private String id;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String instructor;
    private List<String> skills;
    private String thumbnailUrl;
    private int numberOfEnrolledUsers;
    private List<String> categories;
    private Level level;
    private Boolean isPublic;
    private Boolean published;
    private Language language;
    private double rating;
    private List<EnrolledUserInfo> enrolledUsers;
} 