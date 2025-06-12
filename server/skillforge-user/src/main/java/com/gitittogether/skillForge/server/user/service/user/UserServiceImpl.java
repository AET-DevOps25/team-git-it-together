package com.gitittogether.skillForge.server.user.service.user;

import com.gitittogether.skillForge.server.user.config.JwtUtils;
import com.gitittogether.skillForge.server.user.dto.request.user.UserLoginRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserProfileUpdateRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserRegisterRequest;
import com.gitittogether.skillForge.server.user.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.user.dto.response.course.EnrolledCourseResponse;
import com.gitittogether.skillForge.server.user.dto.response.skill.SkillResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserLoginResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserProfileResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserRegisterResponse;
import com.gitittogether.skillForge.server.user.exception.ResourceNotFoundException;
import com.gitittogether.skillForge.server.user.exception.WrongPasswordException;
import com.gitittogether.skillForge.server.user.mapper.course.CategoryMapper;
import com.gitittogether.skillForge.server.user.mapper.course.CourseMapper;
import com.gitittogether.skillForge.server.user.mapper.course.EnrolledCourseMapper;
import com.gitittogether.skillForge.server.user.mapper.skill.SkillMapper;
import com.gitittogether.skillForge.server.user.mapper.user.UserMapper;
import com.gitittogether.skillForge.server.user.model.user.User;
import com.gitittogether.skillForge.server.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

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
    @Transactional
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

    public boolean bookmarkCourse(String userId, CourseResponse courseResponse) {
        if (courseResponse == null || isBlank(userId)) {
            throw new IllegalArgumentException("User ID and/or course response cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getBookmarkedCourses() == null) {
            user.setBookmarkedCourses(new ArrayList<>());
        }
        // Check if the course is already bookmarked
        if (user.getBookmarkedCourses().stream()
                .anyMatch(bookmarkedCourse -> bookmarkedCourse.getId().equals(courseResponse.getId()))) {
            log.warn("User {} has already bookmarked course {}", userId, courseResponse.getId());
            return false; // Course is already bookmarked
        }
        user.getBookmarkedCourses().add(CourseMapper.responseToCourse(courseResponse));
        userRepository.save(user);
        log.info("Bookmarked course {} for user {}", courseResponse.getId(), userId);
        return true;
    }

    public boolean unbookmarkCourse(String userId, CourseResponse courseResponse) {
        if (courseResponse == null || isBlank(userId)) {
            throw new IllegalArgumentException("User ID and/or course response cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getBookmarkedCourses() != null && !user.getBookmarkedCourses().isEmpty()) {
            boolean removed = user.getBookmarkedCourses().removeIf(course -> course.getId().equals(courseResponse.getId()));
            if (!removed) {
                log.warn("Course {} was not found in bookmarked courses for user {}", courseResponse.getId(), userId);
                return false; // Course was not bookmarked
            } else {
                userRepository.save(user);
                log.info("Course {} was successfully unbookmarked for user {}", courseResponse.getId(), userId);
                return true; // Course was successfully unbookmarked
            }
        } else {
            log.warn("User has ne bookmarked courses yet to unbookmark");
            return false; // No bookmarked courses to unbookmark
        }
    }

    @Transactional
    public boolean enrollInCourse(String userId, CourseResponse courseResponse) {
        if (courseResponse == null || isBlank(userId)) {
            throw new IllegalArgumentException("User ID and/or course response cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getEnrolledCourses() == null) {
            user.setEnrolledCourses(new ArrayList<>());
        }
        // Check if the course is already enrolled
        if (user.getEnrolledCourses().stream()
                .anyMatch(enrolledCourse -> enrolledCourse.getCourse().getId().equals(courseResponse.getId()))) {
            log.warn("User {} is already enrolled in course {}", userId, courseResponse.getId());
            return false; // User is already enrolled in the course
        }
        user.getEnrolledCourses().add(CourseMapper.toNewEnrolledCourse(courseResponse, userId));
        userRepository.save(user);
        log.info("Enrolled user {} in course {}", userId, courseResponse.getId());
        return true; // Successfully enrolled in the course
    }

    @Transactional
    public boolean unenrollFromCourse(String userId, EnrolledCourseResponse enrolledCourseResponse) {
        if (enrolledCourseResponse == null || isBlank(userId)) {
            throw new IllegalArgumentException("User ID and/or enrolled course response cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getEnrolledCourses() != null && !user.getEnrolledCourses().isEmpty()) {
            boolean removed = user.getEnrolledCourses().removeIf(course -> course.getCourse().getId().equals(enrolledCourseResponse.getCourse().getId()));
            if (!removed) {
                log.warn("Enrolled course {} was not found in user's enrolled courses", enrolledCourseResponse.getCourse().getId());
                return false; // Course was not enrolled
            }
            userRepository.save(user);
            log.info("Unenrolled user {} from course {}", userId, enrolledCourseResponse.getCourse().getId());
            return true; // Successfully unenrolled from the course
        } else {
            log.warn("User {} has no enrolled courses to unenroll from", userId);
            return false; // No enrolled courses to unenroll from
        }
    }

    @Transactional
    public boolean addSkillToUser(String userId, SkillResponse skillResponse) {
        if (skillResponse == null || isBlank(userId)) {
            throw new IllegalArgumentException("User ID and/or skill cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getSkills() == null) {
            user.setSkills(new ArrayList<>());
        }
        // Check if the skill is already added
        if (user.getSkills().stream()
                .anyMatch(skill -> skill.getId().equals(skillResponse.getId()))) {
            log.warn("User {} already has skill {}", userId, skillResponse.getId());
            return false; // Skill is already added
        }
        user.getSkills().add(SkillMapper.responseToSkill(skillResponse));
        userRepository.save(user);
        log.info("Added skill {} to user {}", skillResponse.getId(), userId);
        return true; // Successfully added the skill
    }

    @Transactional
    public boolean removeSkillFromUser(String userId, SkillResponse skill) {
        if (skill == null || isBlank(userId)) {
            throw new IllegalArgumentException("User ID and/or skill cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getSkills() != null && !user.getSkills().isEmpty()) {
            boolean removed = user.getSkills().removeIf(s -> s.getId().equals(skill.getId()));
            if (!removed) {
                log.warn("Skill {} was not found in user's skills", skill.getId());
                return false; // Skill was not found
            }
            userRepository.save(user);
            log.info("Removed skill {} from user {}", skill.getId(), userId);
            return true; // Successfully removed the skill
        } else {
            log.warn("No skills found for user {}", userId);
            return false; // No skills to remove
        }
    }

    @Transactional
    public boolean addSkillInProgress(String userId, SkillResponse skillResponse) {
        if (skillResponse == null || isBlank(userId)) {
            throw new IllegalArgumentException("User ID and/or skill cannot be null or empty");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getSkillsInProgress() == null) {
            user.setSkillsInProgress(new ArrayList<>());
        }

        // Check if the skill is already in progress
        if (user.getSkillsInProgress().stream()
                .anyMatch(s -> s.getId().equals(skillResponse.getId()))) {
            log.warn("User {} already has skill in progress {}", userId, skillResponse.getId());
            return false; // Skill is already in progress
        }
        user.getSkillsInProgress().add(SkillMapper.responseToSkill(skillResponse));
        userRepository.save(user);
        log.info("Added skill in progress {} to user {}", skillResponse.getId(), userId);
        return true; // Successfully added the skill in progress
    }

    @Transactional
    public boolean removeSkillInProgress(String userId, SkillResponse skill) {
        if (skill == null || isBlank(userId)) {
            throw new IllegalArgumentException("User ID and/or skill cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getSkillsInProgress() != null && !user.getSkillsInProgress().isEmpty()) {
            boolean removed = user.getSkillsInProgress().removeIf(s -> s.getId().equals(skill.getId()));
            if (!removed) {
                log.warn("Skill in progress {} was not found in user's skills in progress", skill.getId());
                return false; // Skill in progress was not found
            }
            userRepository.save(user);
            log.info("Removed skill in progress {} from user {}", skill.getId(), userId);
            return true; // Successfully removed the skill in progress
        } else {
            log.warn("No skills in progress found for user {}", userId);
            return false; // No skills in progress to remove
        }
    }

    @Transactional
    public boolean completeCourse(String userId, EnrolledCourseResponse enrolledCourseResponse) {
        if (enrolledCourseResponse == null || isBlank(userId)) {
            throw new IllegalArgumentException("User ID and/or enrolled course response cannot be null or empty");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getEnrolledCourses() != null && !user.getEnrolledCourses().isEmpty()) {
            boolean removed = user.getEnrolledCourses().removeIf(course -> course.getCourse().getId().equals(enrolledCourseResponse.getCourse().getId()));
            if (!removed) {
                log.warn("Enrolled course {} was not found in user's enrolled courses", enrolledCourseResponse.getCourse().getId());
                return false; // Course was not enrolled
            }
            // ensure complete is set to true
            enrolledCourseResponse.getProgress().setCompleted(true);
            user.getCompletedCourses().add(EnrolledCourseMapper.responseToEnrolledCourse(enrolledCourseResponse));
            userRepository.save(user);
            log.info("Completed course {} for user {}", enrolledCourseResponse.getCourse().getId(), userId);
            return true; // Successfully completed the course
        } else {
            log.warn("User {} has no enrolled courses to complete", userId);
            return false; // No enrolled courses to complete
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

}