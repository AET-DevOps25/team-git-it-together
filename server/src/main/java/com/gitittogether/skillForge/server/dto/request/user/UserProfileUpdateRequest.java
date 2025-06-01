package com.gitittogether.skillForge.server.dto.request;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UserProfileUpdateRequest {
    @Size(max = 256)
    private String bio;

    private String profilePictureUrl;

    @Size(max = 50)
    private String passwordHash;

    private List<CategoryRequest> interests;

    private List<SkillRequest> skills;
    private List<SkillRequest> skillsInProgress;

    private List<EnrolledCourseRequest> enrolledCourses;
    private List<CourseRequest> bookmarkedCourses;

}
