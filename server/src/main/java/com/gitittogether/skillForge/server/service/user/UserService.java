package com.gitittogether.skillForge.server.service.user;

import com.gitittogether.skillForge.server.dto.request.user.UserLoginRequest;
import com.gitittogether.skillForge.server.dto.request.user.UserProfileUpdateRequest;
import com.gitittogether.skillForge.server.dto.request.user.UserRegisterRequest;
import com.gitittogether.skillForge.server.dto.response.user.UserLoginResponse;
import com.gitittogether.skillForge.server.dto.response.user.UserProfileResponse;
import com.gitittogether.skillForge.server.dto.response.user.UserRegisterResponse;

public interface UserService {
    /**
     * Registers a new user with the provided details.
     *
     * @param request The registration request containing user details.
     * @return A response containing the registered user's information.
     */
    UserRegisterResponse registerUser(UserRegisterRequest request);

    /**
     * Authenticates a user with the provided login credentials.
     *
     * @param request The login request containing username/email and password.
     * @return A response containing the authentication token and user details.
     */
    UserLoginResponse authenticateUser(UserLoginRequest request);

    /**
     * Retrieves the profile information of a user by their ID.
     *
     * @param userId The ID of the user whose profile is to be retrieved.
     * @return A response containing the user's profile information.
     */
    UserProfileResponse getUserProfile(String userId);

    /**
     * Updates the profile information of a user.
     *
     * @param userId  The ID of the user whose profile is to be updated.
     * @param request The update request containing new profile details.
     * @return A response containing the updated user's profile information.
     */
    UserProfileResponse updateUserProfile(String userId, UserProfileUpdateRequest request);

    /**
     * Deletes a user by their ID.
     *
     * @param userId The ID of the user to be deleted.
     * @return A boolean indicating whether the deletion was successful.
     */
    boolean deleteUser(String userId);
}
