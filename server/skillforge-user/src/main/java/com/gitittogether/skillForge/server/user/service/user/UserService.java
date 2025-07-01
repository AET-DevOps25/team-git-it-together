package com.gitittogether.skillForge.server.user.service.user;

import com.gitittogether.skillForge.server.user.dto.request.user.UserLoginRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserProfileUpdateRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserRegisterRequest;
import com.gitittogether.skillForge.server.user.dto.response.user.UserLoginResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserProfileResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserRegisterResponse;

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
    UserProfileResponse getUser(String userId);

    /**
     * Updates the profile information of a user.
     *
     * @param userId  The ID of the user whose profile is to be updated.
     * @param request The update request containing new profile details.
     * @return A response containing the updated user's profile information.
     */
    UserProfileResponse updateUser(String userId, UserProfileUpdateRequest request);

    /**
     * Deletes a user by their ID.
     *
     * @param userId The ID of the user to be deleted.
     * @return A boolean indicating whether the deletion was successful.
     */
    boolean deleteUser(String userId);

    /**
     * Bookmarks a course for a user.
     *
     * @param userId The ID of the user.
     * @param courseId The ID of the course to bookmark.
     */
    void bookmarkCourse(String userId, String courseId);

    /**
     * Unbookmarks a course for a user.
     *
     * @param userId The ID of the user.
     * @param courseId The ID of the course to unbookmark.
     */
    void unbookmarkCourse(String userId, String courseId);

    /**
     * Gets all bookmarked course IDs for a user.
     *
     * @param userId The ID of the user.
     * @return List of bookmarked course IDs.
     */
    java.util.List<String> getBookmarkedCourseIds(String userId);

    /**
     * Enrolls a user in a course by adding the courseId to enrolledCourseIds.
     * @param userId The user ID.
     * @param courseId The course ID.
     */
    void enrollUserInCourse(String userId, String courseId);

    /**
     * Unenrolls a user from a course by removing the courseId from enrolledCourseIds.
     * @param userId The user ID.
     * @param courseId The course ID.
     */
    void unenrollUserFromCourse(String userId, String courseId);

    /**
     * Marks a course as completed for a user by adding the courseId to completedCourseIds.
     * @param userId The user ID.
     * @param courseId The course ID.
     */
    void completeCourse(String userId, String courseId);


}
