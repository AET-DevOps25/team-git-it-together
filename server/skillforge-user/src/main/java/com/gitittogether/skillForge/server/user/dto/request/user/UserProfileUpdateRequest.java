package com.gitittogether.skillForge.server.user.dto.request.user;

import com.gitittogether.skillForge.server.user.dto.request.course.CategoryRequest;
import com.gitittogether.skillForge.server.user.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.user.dto.request.course.EnrolledCourseRequest;
import com.gitittogether.skillForge.server.user.dto.request.skill.SkillRequest;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileUpdateRequest {
    @Size(max = 256)
    private String bio;

    private String profilePictureUrl;

    @Size(max = 50)
    private String password;

    private List<CategoryRequest> interests;

    private List<SkillRequest> skills;
    private List<SkillRequest> skillsInProgress;

    private List<EnrolledCourseRequest> enrolledCourses;
    private List<CourseRequest> bookmarkedCourses;
    private List<EnrolledCourseRequest> completedCourses;

}
