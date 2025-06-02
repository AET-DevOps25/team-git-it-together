package com.gitittogether.skillForge.server.mapper.user;

import com.gitittogether.skillForge.server.dto.request.user.UserLoginRequest;
import com.gitittogether.skillForge.server.dto.request.user.UserRegisterRequest;
import com.gitittogether.skillForge.server.dto.response.user.UserLoginResponse;
import com.gitittogether.skillForge.server.dto.response.user.UserProfileResponse;
import com.gitittogether.skillForge.server.dto.response.user.UserRegisterResponse;
import com.gitittogether.skillForge.server.mapper.course.CategoryMapper;
import com.gitittogether.skillForge.server.mapper.course.CourseMapper;
import com.gitittogether.skillForge.server.mapper.course.EnrolledCourseMapper;
import com.gitittogether.skillForge.server.mapper.skill.SkillMapper;
import com.gitittogether.skillForge.server.model.user.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserRegisterRequest toUserRegisterRequest(User user) {
        if (user == null) return null;
        return UserRegisterRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPasswordHash())
                .build();
    }

    public static UserRegisterResponse toUserRegisterResponse(User model) {
        if (model == null) return null;
        return UserRegisterResponse.builder()
                .id(model.getId())
                .firstName(model.getFirstName())
                .lastName(model.getLastName())
                .username(model.getUsername())
                .email(model.getEmail())
                .build();
    }

    public static UserLoginRequest toUserLoginRequest(User model) {
        if (model == null) return null;
        return UserLoginRequest.builder()
                .username(model.getUsername())
                .email(model.getEmail())
                .password(model.getPasswordHash())
                .build();
    }

    public static UserLoginResponse toLoginResponse(User model, String jwtToken) {
        if (model == null) return null;
        return UserLoginResponse.builder()
                .id(model.getId())
                .firstName(model.getFirstName())
                .lastName(model.getLastName())
                .username(model.getUsername())
                .email(model.getEmail())
                .profilePictureUrl(model.getProfilePictureUrl())
                .jwtToken(jwtToken)
                .build();
    }


    public static UserProfileResponse toUserProfileResponse(User model) {
        if (model == null) return null;
        return UserProfileResponse.builder()
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
                .completedCourses(model.getCompletedCourses().stream()
                        .map(EnrolledCourseMapper::toEnrolledCourseResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}