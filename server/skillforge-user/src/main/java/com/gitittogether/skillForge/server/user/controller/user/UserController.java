package com.gitittogether.skillForge.server.user.controller.user;

import com.gitittogether.skillForge.server.user.dto.request.user.UserLoginRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserProfileUpdateRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserRegisterRequest;
import com.gitittogether.skillForge.server.user.dto.response.user.UserLoginResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserProfileResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserRegisterResponse;
import com.gitittogether.skillForge.server.user.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Log4j2
public class UserController {
    private final UserService userService;

    /**
     * User registration endpoint.
     * Accepts a UserRegisterRequest and returns a UserRegisterResponse.
     *
     * @param request The registration request containing user details.
     * @return ResponseEntity with the registration response.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest request) {
        log.info("🔑 Registering user with username: {} and email: {}", request.getUsername(), request.getEmail());
        UserRegisterResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * User login endpoint.
     * Accepts a UserLoginRequest and returns a UserLoginResponse.
     *
     * @param request The login request containing username/email and password.
     * @return ResponseEntity with the login response.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request) {
        log.info("🔑 Logging in user with username: {} or email: {}", request.getUsername(), request.getEmail());
        UserLoginResponse response = userService.authenticateUser(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Fetches the profile of a user by their ID.
     *
     * @param userId The ID of the user whose profile is to be fetched.
     * @return ResponseEntity with the user's profile information.
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
        log.info("🔍 Fetching profile for user ID: {}", userId);
        UserProfileResponse userProfile = userService.getUser(userId);
        if (userProfile == null) {
            log.warn("❌ User profile not found for ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userProfile);
    }

    /**
     * Updates the profile of a user.
     *
     * @param userId  The ID of the user whose profile is to be updated.
     * @param request The update request containing new profile details.
     * @return ResponseEntity with the updated user's profile information.
     */
    @PutMapping("/{userId}/profile")
    public ResponseEntity<?> updateUserProfile(@PathVariable String userId, @Valid @RequestBody UserProfileUpdateRequest request) {
        log.info("🔄 Updating profile for user ID: {}", userId);
        if (request == null) {
            log.warn("❌ Update request is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Update request cannot be null");
        }
        UserProfileResponse userProfile = userService.updateUser(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(userProfile);
    }

    /**
     * Deletes a user profile by their ID.
     *
     * @param userId The ID of the user to be deleted.
     * @return ResponseEntity indicating the result of the deletion operation.
     */
    @DeleteMapping("/{userId}/profile")
    public ResponseEntity<?> deleteUserProfile(@PathVariable String userId) {
        log.info("🗑️ Deleting profile for user ID: {}", userId);
        boolean deleted = userService.deleteUser(userId);
        if (!deleted) {
            log.warn("❌ Failed to delete user profile for ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } else {
            log.info("✅ Successfully deleted user profile for ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    /**
     * Bookmarks a course for a user.
     * This is an inter-service endpoint that allows the course service to bookmark a course for a user.
     * This endpoint is protected and can only be accessed by the course service using a service key.
     *
     * @param userId   The ID of the user.
     * @param courseId The ID of the course to bookmark.
     * @return ResponseEntity indicating the result of the bookmark operation.
     */
    @PostMapping("/{userId}/bookmark/{courseId}")
    public ResponseEntity<?> bookmarkCourse(@PathVariable String userId, @PathVariable String courseId) {
        log.info("Bookmarking course {} for user {} (user service)", courseId, userId);
        userService.bookmarkCourse(userId, courseId);
        return ResponseEntity.ok().build();
    }

    /**
     * Unbookmarks a course for a user.
     * This is an inter-service endpoint that allows the course service to unbookmark a course for a user.
     * This endpoint is protected and can only be accessed by the course service using a service key.
     *
     * @param userId   The ID of the user.
     * @param courseId The ID of the course to unbookmark.
     * @return ResponseEntity indicating the result of the unbookmark operation.
     */
    @DeleteMapping("/{userId}/bookmark/{courseId}")
    public ResponseEntity<?> unbookmarkCourse(@PathVariable String userId, @PathVariable String courseId) {
        log.info("Unbookmarking course {} for user {} (user service)", courseId, userId);
        userService.unbookmarkCourse(userId, courseId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marks a course as completed for a user.
     * This is an inter-service endpoint that allows the course service to mark a course as completed for a user.
     * This endpoint is protected and can only be accessed by the course service using a service key.
     *
     * @param userId   The ID of the user.
     * @param courseId The ID of the course to mark as completed.
     * @return ResponseEntity indicating the result of the complete operation.
     */
    @PostMapping("/{userId}/complete/{courseId}")
    public ResponseEntity<?> completeCourse(@PathVariable String userId, @PathVariable String courseId, @RequestBody List<String> skills) {
        log.info("Marking course {} as completed for user {} (user service)", courseId, userId);
        userService.completeCourse(userId, courseId, skills);
        return ResponseEntity.ok().build();
    }

    /**
     * Gets all bookmarked course IDs for a user.
     *
     * @param userId The ID of the user.
     * @return ResponseEntity with the list of bookmarked course IDs.
     */
    @GetMapping("/{userId}/bookmarks")
    public ResponseEntity<?> getBookmarkedCourseIds(@PathVariable String userId) {
        log.info("🔖 Fetching bookmarked course IDs for user: {}", userId);
        List<String> bookmarkedCourseIds = userService.getBookmarkedCourseIds(userId);
        return ResponseEntity.ok(bookmarkedCourseIds);
    }

    /**
     * Enrolls a user in a course by adding the courseId to enrolledCourseIds.
     * This is an inter-service endpoint that allows the course service to enroll a user in a course.
     * This endpoint is protected and can only be accessed by the course service using a service key.
     *
     * @param userId   The ID of the user.
     * @param courseId The ID of the course to enroll in.
     * @return ResponseEntity indicating the result of the enroll operation.
     */
    @PostMapping("/{userId}/enroll/{courseId}")
    public ResponseEntity<?> enrollUserInCourse(@PathVariable String userId, @PathVariable String courseId, @RequestBody List<String> skills) {
        log.info("Enrolling user {} in course {} (user service)", userId, courseId);
        userService.enrollUserInCourse(userId, courseId, skills);
        return ResponseEntity.ok().build();
    }

    /**
     * Unenrolls a user from a course by removing the courseId from enrolledCourseIds.
     * This is an inter-service endpoint that allows the course service to unenroll a user from a course.
     * This endpoint is protected and can only be accessed by the course service using a service key.
     *
     * @param userId   The ID of the user.
     * @param courseId The ID of the course to unenroll from.
     * @return ResponseEntity indicating the result of the unenroll operation.
     */
    @DeleteMapping("/{userId}/enroll/{courseId}")
    public ResponseEntity<?> unenrollUserFromCourse(@PathVariable String userId, @PathVariable String courseId, @RequestBody List<String> skills) {
        log.info("Unenrolling user {} from course {} (user service)", userId, courseId);
        userService.unenrollUserFromCourse(userId, courseId, skills);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/skills")
    public ResponseEntity<?> getUserSkills(@PathVariable String userId) {
        log.info("Fetching skills for user: {}", userId);
        return ResponseEntity.ok(userService.getUserSkills(userId));
    }

    @GetMapping("/{userId}/skills-in-progress")
    public ResponseEntity<?> getUserSkillsInProgress(@PathVariable String userId) {
        log.info("Fetching skills in progress for user: {}", userId);
        return ResponseEntity.ok(userService.getUserSkillsInProgress(userId));
    }

    @GetMapping("/{userId}/courses/enrolled")
    public ResponseEntity<?> getUserEnrolledCourses(@PathVariable String userId) {
        log.info("Fetching enrolled course IDs for user: {}", userId);
        return ResponseEntity.ok(userService.getUserEnrolledCourseIds(userId));
    }

    @GetMapping("/{userId}/courses/completed")
    public ResponseEntity<?> getUserCompletedCourses(@PathVariable String userId) {
        log.info("Fetching completed course IDs for user: {}", userId);
        return ResponseEntity.ok(userService.getUserCompletedCourseIds(userId));
    }

    @GetMapping("/{userId}/courses/bookmarked")
    public ResponseEntity<?> getUserBookmarkedCourses(@PathVariable String userId) {
        log.info("Fetching bookmarked course IDs for user: {}", userId);
        return ResponseEntity.ok(userService.getUserBookmarkedCourseIds(userId));
    }

    /**
     * Fetches users with a specific skill.
     *
     * @param skill The skill to filter users by.
     * @return ResponseEntity with the list of users having the specified skill.
     */
    @GetMapping("/with-skill")
    public ResponseEntity<?> getUsersWithSkill(@RequestParam String skill) {
        log.info("Fetching users with skill: {}", skill);
        return ResponseEntity.ok(userService.getUsersWithSkill(skill));
    }

    /**
     * Fetches users with a specific skill in progress.
     *
     * @param skill The skill to filter users by.
     * @return ResponseEntity with the list of users having the specified skill in progress.
     */
    @GetMapping("/with-skill-in-progress")
    public ResponseEntity<?> getUsersWithSkillInProgress(@RequestParam String skill) {
        log.info("Fetching users with skill in progress: {}", skill);
        return ResponseEntity.ok(userService.getUsersWithSkillInProgress(skill));
    }

    /**
     * Fetches users enrolled in a specific course.
     *
     * @param courseId The ID of the course to filter users by.
     * @return ResponseEntity with the list of users enrolled in the specified course.
     */
    @GetMapping("/enrolled-in/{courseId}")
    public ResponseEntity<?> getUsersEnrolledInCourse(@PathVariable String courseId) {
        log.info("Fetching users enrolled in course: {}", courseId);
        return ResponseEntity.ok(userService.getUsersEnrolledInCourse(courseId));
    }

    /**
     * Fetches users who have completed a specific course.
     *
     * @param courseId The ID of the course to filter users by.
     * @return ResponseEntity with the list of users who completed the specified course.
     */
    @GetMapping("/completed/{courseId}")
    public ResponseEntity<?> getUsersCompletedCourse(@PathVariable String courseId) {
        log.info("Fetching users who completed course: {}", courseId);
        return ResponseEntity.ok(userService.getUsersCompletedCourse(courseId));
    }

    /**
     * Fetches users who have bookmarked a specific course.
     *
     * @param courseId The ID of the course to filter users by.
     * @return ResponseEntity with the list of users who bookmarked the specified course.
     */
    @GetMapping("/bookmarked/{courseId}")
    public ResponseEntity<?> getUsersBookmarkedCourse(@PathVariable String courseId) {
        log.info("Fetching users who bookmarked course: {}", courseId);
        return ResponseEntity.ok(userService.getUsersBookmarkedCourse(courseId));
    }

    @GetMapping("/search/user/{username}")
    public ResponseEntity<?> searchUsersByUsername(@PathVariable String username) {
        log.info("Searching users by username: {}", username);
        List<UserProfileResponse> users = userService.searchUsersByUsername(username);
        if (users.isEmpty()) {
            log.warn("No users found with username: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found");
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search/email/{email}")
    public ResponseEntity<?> searchUsersByEmail(@PathVariable String email) {
        log.info("Searching users by email: {}", email);
        List<UserProfileResponse> users = userService.searchUsersByEmail(email);
        if (users.isEmpty()) {
            log.warn("No users found with email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found");
        }
        return ResponseEntity.ok(users);
    }

}
