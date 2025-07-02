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
     *
     * @param userId   The ID of the user.
     * @param courseId The ID of the course to mark as completed.
     * @return ResponseEntity indicating the result of the complete operation.
     */
    @PostMapping("/{userId}/complete/{courseId}")
    public ResponseEntity<?> completeCourse(@PathVariable String userId, @PathVariable String courseId) {
        log.info("Marking course {} as completed for user {} (user service)", courseId, userId);
        userService.completeCourse(userId, courseId);
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
     *
     * @param userId   The ID of the user.
     * @param courseId The ID of the course to enroll in.
     * @return ResponseEntity indicating the result of the enroll operation.
     */
    @PostMapping("/{userId}/enroll/{courseId}")
    public ResponseEntity<?> enrollUserInCourse(@PathVariable String userId, @PathVariable String courseId) {
        log.info("Enrolling user {} in course {} (user service)", userId, courseId);
        userService.enrollUserInCourse(userId, courseId);
        return ResponseEntity.ok().build();
    }

    /**
     * Unenrolls a user from a course by removing the courseId from enrolledCourseIds.
     *
     * @param userId   The ID of the user.
     * @param courseId The ID of the course to unenroll from.
     * @return ResponseEntity indicating the result of the unenroll operation.
     */
    @DeleteMapping("/{userId}/enroll/{courseId}")
    public ResponseEntity<?> unenrollUserFromCourse(@PathVariable String userId, @PathVariable String courseId) {
        log.info("Unenrolling user {} from course {} (user service)", userId, courseId);
        userService.unenrollUserFromCourse(userId, courseId);
        return ResponseEntity.noContent().build();
    }
}
