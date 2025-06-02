package com.gitittogether.skillForge.server.controller.user;

import com.gitittogether.skillForge.server.dto.request.user.UserLoginRequest;
import com.gitittogether.skillForge.server.dto.request.user.UserProfileUpdateRequest;
import com.gitittogether.skillForge.server.dto.request.user.UserRegisterRequest;
import com.gitittogether.skillForge.server.dto.response.user.UserLoginResponse;
import com.gitittogether.skillForge.server.dto.response.user.UserRegisterResponse;
import com.gitittogether.skillForge.server.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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
        return ResponseEntity.ok(userService.getUserProfile(userId));
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
        return ResponseEntity.ok(userService.updateUserProfile(userId, request));
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
}
