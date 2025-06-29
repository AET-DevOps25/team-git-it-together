package com.gitittogether.skillForge.server.user.dto.response.user;

import com.gitittogether.skillForge.server.user.dto.response.skill.SkillResponse;
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
public class UserProfileResponse {
    @NotBlank
    private String id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    private String profilePictureUrl;
    private String bio;
    private List<SkillResponse> skills;
    private List<SkillResponse> skillsInProgress;
    
    // Course references - now using IDs instead of full objects
    private List<String> enrolledCourseIds;
    private List<String> bookmarkedCourseIds;
    private List<String> completedCourseIds;
}