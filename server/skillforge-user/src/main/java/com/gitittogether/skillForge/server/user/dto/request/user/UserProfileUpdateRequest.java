package com.gitittogether.skillForge.server.user.dto.request.user;

import com.gitittogether.skillForge.server.user.dto.request.skill.SkillRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String profilePictureUrl;
    private String bio;
    private List<SkillRequest> skills;
    private List<SkillRequest> skillsInProgress;
    
    // Course references - now using IDs instead of full objects
    private List<String> enrolledCourseIds;
    private List<String> bookmarkedCourseIds;
    private List<String> completedCourseIds;
}
