package com.gitittogether.skillForge.server.user.dto.response.user;

import com.gitittogether.skillForge.server.user.dto.response.course.CategoryResponse;
import com.gitittogether.skillForge.server.user.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.user.dto.response.course.EnrolledCourseResponse;
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
    private List<CategoryResponse> interests;
    private List<SkillResponse> skills;
    private List<SkillResponse> skillsInProgress;
    private List<EnrolledCourseResponse> enrolledCourses;
    private List<CourseResponse> bookmarkedCourses;
    private List<EnrolledCourseResponse> completedCourses;
}