package com.gitittogether.skillForge.server.user.mapper.user;

import com.gitittogether.skillForge.server.user.dto.request.user.UserLoginRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserRegisterRequest;
import com.gitittogether.skillForge.server.user.dto.response.user.UserLoginResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserProfileResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserRegisterResponse;
import com.gitittogether.skillForge.server.user.mapper.skill.SkillMapper;
import com.gitittogether.skillForge.server.user.model.user.User;

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

    public static UserLoginResponse toUserLoginResponse(User model, String jwtToken) {
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
                .skills(
                        model.getSkills() != null ? model.getSkills().stream()
                                .map(SkillMapper::toSkillResponse)
                                .collect(Collectors.toList()) : null
                )
                .skillsInProgress(
                        model.getSkillsInProgress() != null ? model.getSkillsInProgress().stream()
                                .map(SkillMapper::toSkillResponse)
                                .collect(Collectors.toList()) : null
                )
                .enrolledCourseIds(model.getEnrolledCourseIds())
                .bookmarkedCourseIds(model.getBookmarkedCourseIds())
                .completedCourseIds(model.getCompletedCourseIds())
                .build();
    }
}