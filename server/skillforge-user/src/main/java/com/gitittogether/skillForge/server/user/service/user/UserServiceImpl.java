package com.gitittogether.skillForge.server.user.service.user;

import com.gitittogether.skillForge.server.user.config.JwtUtils;
import com.gitittogether.skillForge.server.user.dto.request.user.UserLoginRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserProfileUpdateRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserRegisterRequest;
import com.gitittogether.skillForge.server.user.dto.response.user.UserLoginResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserProfileResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserRegisterResponse;
import com.gitittogether.skillForge.server.user.exception.ResourceNotFoundException;
import com.gitittogether.skillForge.server.user.exception.WrongPasswordException;
import com.gitittogether.skillForge.server.user.mapper.user.UserMapper;
import com.gitittogether.skillForge.server.user.model.user.User;
import com.gitittogether.skillForge.server.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        log.info("Registered user with ID: {}", savedUser.getId());

        return UserMapper.toUserRegisterResponse(savedUser);
    }

    @Override
    public UserLoginResponse authenticateUser(UserLoginRequest request) {
        log.info("Authenticating user: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new WrongPasswordException("Invalid password");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        log.info("Authenticated user: {}", user.getUsername());

        return UserMapper.toUserLoginResponse(user, token);
    }

    @Override
    public UserProfileResponse getUser(String userId) {
        log.info("Fetching user profile: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserMapper.toUserProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUser(String userId, UserProfileUpdateRequest request) {
        log.info("Updating user profile: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update basic fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        User savedUser = userRepository.save(user);
        log.info("Updated user profile: {}", savedUser.getId());

        return UserMapper.toUserProfileResponse(savedUser);
    }

    @Override
    @Transactional
    public boolean deleteUser(String userId) {
        if (isBlank(userId)) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
        log.info("Deleted user with ID: {}", userId);
        return true;
    }

    @Override
    @Transactional
    public void bookmarkCourse(String userId, String courseId) {
        log.info("Bookmarking course {} for user {}", courseId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getBookmarkedCourseIds() == null) {
            user.setBookmarkedCourseIds(new ArrayList<>());
        }

        if (user.getBookmarkedCourseIds().contains(courseId)) {
            log.warn("User {} has already bookmarked course {}", userId, courseId);
            throw new IllegalArgumentException("Course is already bookmarked");
        }

        user.getBookmarkedCourseIds().add(courseId);
        userRepository.save(user);
        log.info("Bookmarked course {} for user {}", courseId, userId);
    }

    @Override
    @Transactional
    public void unbookmarkCourse(String userId, String courseId) {
        log.info("Unbookmarking course {} for user {}", courseId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getBookmarkedCourseIds() == null || !user.getBookmarkedCourseIds().contains(courseId)) {
            log.warn("Course {} is not bookmarked for user {}", courseId, userId);
            throw new IllegalArgumentException("Course is not bookmarked");
        }

        user.getBookmarkedCourseIds().remove(courseId);
        userRepository.save(user);
        log.info("Unbookmarked course {} for user {}", courseId, userId);
    }

    @Override
    public java.util.List<String> getBookmarkedCourseIds(String userId) {
        log.info("Fetching bookmarked course IDs for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return user.getBookmarkedCourseIds() != null ? user.getBookmarkedCourseIds() : new ArrayList<>();
    }

    @Override
    @Transactional
    public void enrollUserInCourse(String userId, String courseId, List<String> skills) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getEnrolledCourseIds() == null) {
            user.setEnrolledCourseIds(new ArrayList<>());
        }
        if (!user.getEnrolledCourseIds().contains(courseId)) {
            user.getEnrolledCourseIds().add(courseId);
            if (user.getSkillsInProgress() == null) {
                user.setSkillsInProgress(new ArrayList<>());
            }
            if (skills != null && !skills.isEmpty()) {
                user.getSkillsInProgress().addAll(skills);
            }
            userRepository.save(user);
            log.info("Enrolled user {} in course {}", userId, courseId);
        } else {
            log.info("User {} is already enrolled in course {}", userId, courseId);
        }
    }

    @Override
    @Transactional
    public void unenrollUserFromCourse(String userId, String courseId, List<String> skills) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getEnrolledCourseIds() != null && user.getEnrolledCourseIds().contains(courseId)) {
            user.getEnrolledCourseIds().remove(courseId);
            if (user.getSkillsInProgress() != null && skills != null) {
                user.getSkillsInProgress().removeAll(skills);
            }
            userRepository.save(user);
            log.info("Unenrolled user {} from course {}", userId, courseId);
        } else {
            log.info("User {} is not enrolled in course {}", userId, courseId);
        }
    }

    @Override
    @Transactional
    public void completeCourse(String userId, String courseId, List<String> skills) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getCompletedCourseIds() == null) {
            user.setCompletedCourseIds(new ArrayList<>());
        }
        if (!user.getCompletedCourseIds().contains(courseId)) {
            user.getCompletedCourseIds().add(courseId);
            if (user.getSkillsInProgress() == null) {
                user.setSkillsInProgress(new ArrayList<>());
            }
            if (skills != null && !skills.isEmpty()) {
                user.getSkillsInProgress().removeAll(skills);
                user.getSkills().addAll(skills);
            }
            userRepository.save(user);
            log.info("Marked course {} as completed for user {}", courseId, userId);
        } else {
            log.info("Course {} is already marked as completed for user {}", courseId, userId);
        }
    }

    @Override
    public List<String> getUserSkills(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getSkills() == null ? List.of() : user.getSkills();
    }

    @Override
    public List<String> getUserSkillsInProgress(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getSkillsInProgress() == null ? List.of() : user.getSkillsInProgress();
    }

    @Override
    public List<String> getUserEnrolledCourseIds(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getEnrolledCourseIds() == null ? List.of() : user.getEnrolledCourseIds();
    }

    @Override
    public List<String> getUserCompletedCourseIds(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getCompletedCourseIds() == null ? List.of() : user.getCompletedCourseIds();
    }

    @Override
    public List<String> getUserBookmarkedCourseIds(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getBookmarkedCourseIds() == null ? List.of() : user.getBookmarkedCourseIds();
    }

    @Override
    public List<UserProfileResponse> getUsersWithSkill(String skillName) {
        return userRepository.findBySkillsName(skillName).stream().map(UserMapper::toUserProfileResponse).toList();
    }

    @Override
    public List<UserProfileResponse> getUsersWithSkillInProgress(String skillName) {
        return userRepository.findBySkillsInProgressName(skillName).stream().map(UserMapper::toUserProfileResponse).toList();
    }

    @Override
    public List<UserProfileResponse> getUsersEnrolledInCourse(String courseId) {
        return userRepository.findByEnrolledCourseIdsContaining(courseId).stream().map(UserMapper::toUserProfileResponse).toList();
    }

    @Override
    public List<UserProfileResponse> getUsersCompletedCourse(String courseId) {
        return userRepository.findByCompletedCourseIdsContaining(courseId).stream().map(UserMapper::toUserProfileResponse).toList();
    }

    @Override
    public List<UserProfileResponse> getUsersBookmarkedCourse(String courseId) {
        return userRepository.findByBookmarkedCourseIdsContaining(courseId).stream().map(UserMapper::toUserProfileResponse).toList();
    }

    @Override
    public List<UserProfileResponse> searchUsersByUsername(String username) {
        if (isBlank(username)) {
            return List.of();
        }
        return userRepository.findUserByUsernameContainingIgnoreCase(username).stream()
                .map(UserMapper::toUserProfileResponse)
                .toList();
    }

    @Override
    public List<UserProfileResponse> searchUsersByEmail(String email) {
        if (isBlank(email)) {
            return List.of();
        }
        return userRepository.findUserByEmailContainingIgnoreCase(email).stream()
                .map(UserMapper::toUserProfileResponse)
                .toList();
    }

    /**
     * Checks if a string is blank (null or empty after trimming).
     *
     * @param str The string to check.
     * @return true if the string is blank, false otherwise.
     */
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

}