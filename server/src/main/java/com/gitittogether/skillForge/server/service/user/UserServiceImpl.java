package com.gitittogether.skillForge.server.service.user.impl;

import com.gitittogether.skillForge.server.dto.request.UserLoginRequest;
import com.gitittogether.skillForge.server.dto.request.UserProfileUpdateRequest;
import com.gitittogether.skillForge.server.dto.request.UserRegisterRequest;
import com.gitittogether.skillForge.server.dto.response.user.UserLoginResponse;
import com.gitittogether.skillForge.server.dto.response.user.UserProfileResponse;
import com.gitittogether.skillForge.server.dto.response.user.UserRegisterResponse;
import com.gitittogether.skillForge.server.mapper.course.CategoryMapper;
import com.gitittogether.skillForge.server.mapper.course.CourseMapper;
import com.gitittogether.skillForge.server.mapper.course.EnrolledCourseMapper;
import com.gitittogether.skillForge.server.mapper.course.SkillMapper;
import com.gitittogether.skillForge.server.mapper.user.UserMapper;
import com.gitittogether.skillForge.server.model.user.User;
import com.gitittogether.skillForge.server.repository.user.UserRepository;
import com.gitittogether.skillForge.server.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Invalid registration request");
        }
        // Check if the user already exists
        if (userRepository.existsByUsername(request.getUsername()) ||
                userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User already exists with the provided username or email");
        }
        // Create a new user entity
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(hashedPassword)
                .build();
        // Save the user to the repository
        User savedUser = userRepository.save(newUser);
        return new UserRegisterResponse(savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getUsername(), savedUser.getEmail());
    }

    @Override
    public UserLoginResponse authenticateUser(UserLoginRequest request) {
        if (request == null || (request.getEmail() == null && request.getUsername() == null) || request.getPassword() == null) {
            throw new IllegalArgumentException("Invalid login request");
        }
        User user;
        if (request.getUsername() != null) {
            // Find the user by username
            user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with the provided username"));
        } else {
            // Find the user by email
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with the provided email"));
        }
        // Verify the password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password");
        }
        // if all fine we generate a jwt token and return the user login response
        // TODO: Implement JWT token generation
        // For now, we will return a dummy token
        String dummyToken = "dummy-jwt-token";

        return new UserLoginResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getProfilePictureUrl(),
                dummyToken
        );
    }

    @Override
    public UserProfileResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserMapper.toUserProfileResponse(user);
    }

    @Override
    public UserProfileResponse updateUserProfile(String userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (request.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getInterests() != null) {
            user.setInterests(
                    request.getInterests().stream()
                            .map(CategoryMapper::requestToCategory)
                            .toList());
        }
        if (request.getSkills() != null) {
            user.setSkills(
                    request.getSkills().stream()
                            .map(SkillMapper::requestToSkill)
                            .toList());
        }

        if (request.getSkillsInProgress() != null) {
            user.setSkillsInProgress(
                    request.getSkillsInProgress().stream()
                            .map(SkillMapper::requestToSkill)
                            .toList());
        }

        if (request.getEnrolledCourses() != null) {
            user.setEnrolledCourses(
                    request.getEnrolledCourses().stream()
                            .map(EnrolledCourseMapper::requestToEnrolledCourse)
                            .toList());
        }

        if (request.getBookmarkedCourses() != null) {
            user.setBookmarkedCourses(
                    request.getBookmarkedCourses().stream()
                            .map(CourseMapper::requestToCourse)
                            .toList());
        }
        // Save the updated user
        User updatedUser = userRepository.save(user);
        // Return the updated user profile response
        return UserMapper.toUserProfileResponse(updatedUser);
    }

}