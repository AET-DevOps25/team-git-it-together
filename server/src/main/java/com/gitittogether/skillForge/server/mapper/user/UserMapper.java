package com.gitittogether.skillForge.server.mapper.user;

import com.gitittogether.skillForge.server.mapper.course.CategoryMapper;
import com.gitittogether.skillForge.server.mapper.course.CourseMapper;
import com.gitittogether.skillForge.server.mapper.course.EnrolledCourseMapper;
import com.gitittogether.skillForge.server.mapper.course.SkillMapper;
import com.gitittogether.skillForge.server.model.user.User;

import java.util.stream.Collectors;

public class UserProfileResponse {

    public static com.gitittogether.skillForge.server.dto.response.user.UserProfileResponse toUserProfileResponse(User model) {
        if (model == null) return null;
        return com.gitittogether.skillForge.server.dto.response.user.UserProfileResponse.builder()
                .id(model.getId())
                .firstName(model.getFirstName())
                .lastName(model.getLastName())
                .username(model.getUsername())
                .email(model.getEmail())
                .profilePictureUrl(model.getProfilePictureUrl())
                .bio(model.getBio())
                .interests(
                        model.getInterests().stream()
                                .map(CategoryMapper::toCategoryResponse)
                                .collect(Collectors.toList())
                )
                .skills(
                        model.getSkills().stream()
                                .map(SkillMapper::toSkillResponse)
                                .collect(Collectors.toList())
                )
                .skillsInProgress(
                        model.getSkillsInProgress().stream()
                                .map(SkillMapper::toSkillResponse)
                                .collect(Collectors.toList())
                )
                .enrolledCourses(
                        model.getEnrolledCourses().stream()
                                .map(EnrolledCourseMapper::toEnrolledCourseResponse)
                                .collect(Collectors.toList())
                )
                .bookmarkedCourses(
                        model.getBookmarkedCourses().stream()
                                .map(CourseMapper::toCourseResponse)
                                .collect(Collectors.toList())
                )
                .build();
    }
}