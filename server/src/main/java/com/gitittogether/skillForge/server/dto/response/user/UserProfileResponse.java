package com.gitittogether.skillForge.server.dto.response.user;

import com.gitittogether.skillForge.server.dto.response.course.CategoryResponse;
import com.gitittogether.skillForge.server.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.dto.response.course.EnrolledCourseResponse;
import com.gitittogether.skillForge.server.dto.response.skill.SkillResponse;
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
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String profilePictureUrl;
    private String bio;
    private List<CategoryResponse> interests;
    private List<SkillResponse> skills;
    private List<SkillResponse> skillsInProgress;
    private List<EnrolledCourseResponse> enrolledCourses;
    private List<CourseResponse> bookmarkedCourses;
}