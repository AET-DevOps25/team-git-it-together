package com.gitittogether.skillForge.server.service.user;

import com.gitittogether.skillForge.server.config.JwtUtils;
import com.gitittogether.skillForge.server.dto.request.user.UserLoginRequest;
import com.gitittogether.skillForge.server.dto.request.user.UserProfileUpdateRequest;
import com.gitittogether.skillForge.server.dto.request.user.UserRegisterRequest;
import com.gitittogether.skillForge.server.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.dto.response.course.EnrolledCourseResponse;
import com.gitittogether.skillForge.server.dto.response.skill.SkillResponse;
import com.gitittogether.skillForge.server.dto.response.user.UserLoginResponse;
import com.gitittogether.skillForge.server.dto.response.user.UserProfileResponse;
import com.gitittogether.skillForge.server.dto.response.user.UserRegisterResponse;
import com.gitittogether.skillForge.server.exception.ResourceNotFoundException;
import com.gitittogether.skillForge.server.exception.WrongPasswordException;
import com.gitittogether.skillForge.server.mapper.course.CategoryMapper;
import com.gitittogether.skillForge.server.mapper.course.CourseMapper;
import com.gitittogether.skillForge.server.mapper.course.EnrolledCourseMapper;
import com.gitittogether.skillForge.server.mapper.skill.SkillMapper;
import com.gitittogether.skillForge.server.mapper.user.UserMapper;
import com.gitittogether.skillForge.server.model.user.User;
import com.gitittogether.skillForge.server.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;


    @Override
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        if (request == null ||
                isBlank(request.getFirstName()) ||
                isBlank(request.getLastName()) ||
                isBlank(request.getUsername()) ||
                isBlank(request.getEmail()) ||
                isBlank(request.getPassword())) {
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
        if (request == null
                || (isBlank(request.getEmail()) && isBlank(request.getUsername()))
                || isBlank(request.getPassword())) {
            throw new IllegalArgumentException("Invalid login request: Provide email or username, and a password.");
        }
        User user = null;
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            // Find the user by username
            user = userRepository.findByUsername(request.getUsername())
                    .orElse(null);
        } else if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Find the user by email
            user = userRepository.findByEmail(request.getEmail())
                    .orElse(null);
        }

        // If user is not found, throw an exception
        if (user == null) {
            throw new ResourceNotFoundException("User not found with the provided username or email");
        }

        // Verify the password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new WrongPasswordException("Invalid password! Please try again.");
        }
        // Generate JWT token
        String jwtToken = jwtUtils.generateToken(user.getId(), user.getUsername());

        return UserLoginResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePictureUrl(user.getProfilePictureUrl())
                .jwtToken(jwtToken)
                .build();
    }

    @Override
    public UserProfileResponse getUserProfile(String userId) {
        if (isBlank(userId)) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toUserProfileResponse(user);
    }

    @Override
    public UserProfileResponse updateUserProfile(String userId, UserProfileUpdateRequest request) {
        if (isBlank(userId) || request == null) {
            throw new IllegalArgumentException("User ID and update request cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getProfilePictureUrl() != null) { // profile picture URL can be blank (empty string -> no picture)
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }

        if (request.getBio() != null) { // bio can be blank (empty string)
            user.setBio(request.getBio());
        }

        if (!isBlank(request.getPassword())) { //if password is provided, in the request, then it should not be blank (null or empty)
            String hashedPassword = passwordEncoder.encode(request.getPassword());
            user.setPasswordHash(hashedPassword);
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

    @Override
    public boolean deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
        log.info("Deleted user with ID: {}", userId);
        return true;
    }

    public void bookmarkCourse(String userId, CourseResponse courseResponse) {
        if (courseResponse == null) {
            throw new IllegalArgumentException("Course response cannot be null");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getBookmarkedCourses() == null) {
            user.setBookmarkedCourses(new ArrayList<>());
        }
        user.getBookmarkedCourses().add(CourseMapper.responseToCourse(courseResponse));
        userRepository.save(user);
        log.info("Bookmarked course {} for user {}", courseResponse.getId(), userId);
    }

    public void unbookmarkCourse(String userId, CourseResponse courseResponse) {
        if (courseResponse == null) {
            throw new IllegalArgumentException("Course response cannot be null");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getBookmarkedCourses() != null) {
            user.getBookmarkedCourses().removeIf(course -> course.getId().equals(courseResponse.getId()));
            userRepository.save(user);
            log.info("Unbookmarked course {} for user {}", courseResponse.getId(), userId);
        } else {
            log.warn("No bookmarked courses found for user {}", userId);
        }
    }

    public void enrollInCourse(String userId, CourseResponse courseResponse) {
        if (courseResponse == null) {
            throw new IllegalArgumentException("Course response cannot be null");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getEnrolledCourses() == null) {
            user.setEnrolledCourses(new ArrayList<>());
        }
        user.getEnrolledCourses().add(CourseMapper.toNewEnrolledCourse(courseResponse, userId));
        userRepository.save(user);
        log.info("Enrolled user {} in course {}", userId, courseResponse.getId());
    }

    public void unenrollFromCourse(String userId, EnrolledCourseResponse enrolledCourseResponse) {
        if (enrolledCourseResponse == null) {
            throw new IllegalArgumentException("Course response cannot be null");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getEnrolledCourses() != null) {
            user.getEnrolledCourses().removeIf(course -> course.getCourse().getId().equals(enrolledCourseResponse.getCourse().getId()));
            userRepository.save(user);
            log.info("Unenrolled user {} from course {}", userId, enrolledCourseResponse.getCourse().getId());
        } else {
            log.warn("No enrolled courses found for user {}", userId);
        }
    }

    public void addSkillToUser(String userId, SkillResponse skill) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getSkills() == null) {
            user.setSkills(new ArrayList<>());
        }
        user.getSkills().add(SkillMapper.responseToSkill(skill));
        userRepository.save(user);
        log.info("Added skill {} to user {}", skill.getId(), userId);
    }

    public void removeSkillFromUser(String userId, SkillResponse skill) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getSkills() != null) {
            user.getSkills().removeIf(s -> s.getId().equals(skill.getId()));
            userRepository.save(user);
            log.info("Removed skill {} from user {}", skill.getId(), userId);
        } else {
            log.warn("No skills found for user {}", userId);
        }
    }

    public void addSkillInProgress(String userId, SkillResponse skill) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getSkillsInProgress() == null) {
            user.setSkillsInProgress(new ArrayList<>());
        }
        user.getSkillsInProgress().add(SkillMapper.responseToSkill(skill));
        userRepository.save(user);
        log.info("Added skill in progress {} to user {}", skill.getId(), userId);
    }

    public void removeSkillInProgress(String userId, SkillResponse skill) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getSkillsInProgress() != null) {
            user.getSkillsInProgress().removeIf(s -> s.getId().equals(skill.getId()));
            userRepository.save(user);
            log.info("Removed skill in progress {} from user {}", skill.getId(), userId);
        } else {
            log.warn("No skills in progress found for user {}", userId);
        }
    }

    public void completeCourse(String userId, EnrolledCourseResponse enrolledCourseResponse) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getEnrolledCourses() != null) {
            user.getEnrolledCourses().removeIf(course -> course.getCourse().getId().equals(enrolledCourseResponse.getCourse().getId()));
            // ensure complete is set to true
            enrolledCourseResponse.getProgress().setCompleted(true);
            user.getCompletedCourses().add(EnrolledCourseMapper.responseToEnrolledCourse(enrolledCourseResponse));
            userRepository.save(user);
            log.info("Completed course {} for user {}", enrolledCourseResponse.getCourse().getId(), userId);
        } else {
            log.warn("No enrolled courses found for user {}", userId);
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }


}