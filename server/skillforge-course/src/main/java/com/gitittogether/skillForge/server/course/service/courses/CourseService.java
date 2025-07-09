package com.gitittogether.skillForge.server.course.service.courses;

import com.gitittogether.skillForge.server.course.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.course.dto.request.course.LearningPathRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseSummaryResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.EnrolledUserInfoResponse;

import java.util.List;

public interface CourseService {

    /**
     * Creates a new course.
     *
     * @param request The course creation request.
     * @return The created course response.
     */
    CourseResponse createCourse(CourseRequest request);

    /**
     * Retrieves a course by its ID.
     *
     * @param courseId The ID of the course to retrieve.
     * @return The course response.
     */
    CourseResponse getCourse(String courseId);

    /**
     * Retrieves all courses.
     *
     * @return List of all course responses.
     */
    List<CourseSummaryResponse> getAllCourses();

    /**
     * Retrieves all public courses for landing page display.
     *
     * @return List of public course responses.
     */
    List<CourseSummaryResponse> getPublicCourses();

    /**
     * Retrieves all public and published courses for landing page display.
     *
     * @return List of public and published course responses.
     */
    List<CourseResponse> getPublicPublishedCourses();

    /**
     * Retrieves all private courses for authenticated users.
     * Private courses are those not published and not visible to the public.
     *
     * @return List of private course responses.
     */
    List<CourseResponse> getPrivateCourses();

    /**
     * Updates an existing course.
     *
     * @param courseId The ID of the course to update.
     * @param request  The course update request.
     * @return The updated course response.
     */
    CourseResponse updateCourse(String courseId, CourseRequest request);

    /**
     * Deletes a course.
     *
     * @param courseId The ID of the course to delete.
     */
    void deleteCourse(String courseId);

    /**
     * Enrolls a user in a course.
     *
     * @param courseId The ID of the course to enroll in.
     * @param userId   The ID of the user to enroll.
     * @return The enrolled course response.
     */
    CourseResponse enrollUserInCourse(String courseId, String userId);

    /**
     * Unenrolls a user from a course.
     *
     * @param courseId The ID of the course to unenroll from.
     * @param userId   The ID of the user to unenroll.
     */
    void unenrollUserFromCourse(String courseId, String userId);

    /**
     * Marks a course as completed for a user.
     *
     * @param courseId The ID of the course to complete.
     * @param userId   The ID of the user.
     */
    void completeCourseForUser(String courseId, String userId);

    /**
     * Retrieves all courses a user is enrolled in.
     *
     * @param userId The ID of the user.
     * @return List of enrolled course responses.
     */
    List<EnrolledUserInfoResponse> getUserEnrolledCourses(String userId);

    /**
     * Bookmarks a course for a user.
     *
     * @param courseId The ID of the course to bookmark.
     * @param userId   The ID of the user.
     */
    void bookmarkCourse(String courseId, String userId);

    /**
     * Unbookmarks a course for a user.
     *
     * @param courseId The ID of the course to unbookmark.
     * @param userId   The ID of the user.
     */
    void unbookmarkCourse(String courseId, String userId);

    /**
     * Searches for courses by title.
     * * @param title The title to search for.
     *
     * @return List of course responses matching the title.
     */
    List<CourseResponse> searchCoursesByTitle(String title);

    /**
     * Retrieves all courses by instructor.
     *
     * @param instructor The instructor ID or name.
     * @return List of course responses.
     */
    List<CourseResponse> getCoursesByInstructor(String instructor);

    /**
     * Retrieves all courses by level.
     *
     * @param level The course level.
     * @return List of course responses.
     */
    List<CourseResponse> getCoursesByLevel(com.gitittogether.skillForge.server.course.model.utils.Level level);

    /**
     * Retrieves all courses by language.
     *
     * @param language The course language.
     * @return List of course responses.
     */
    List<CourseResponse> getCoursesByLanguage(com.gitittogether.skillForge.server.course.model.utils.Language language);

    /**
     * Retrieves all courses containing a specific skill.
     *
     * @param skillName The skill name.
     * @return List of course responses.
     */
    List<CourseResponse> getCoursesBySkill(String skillName);

    /**
     * Retrieves all courses containing a specific category.
     *
     * @param categoryName The category name.
     * @return List of course responses.
     */
    List<CourseResponse> getCoursesByCategory(String categoryName);

    /**
     * Retrieves all courses with a fuzzy title search (case-insensitive).
     *
     * @param title The title substring.
     * @return List of course responses.
     */
    List<CourseResponse> searchCoursesByTitleFuzzy(String title);

    /**
     * Flexible search for courses by any combination of criteria. All parameters are optional.
     *
     * @param instructor      The instructor ID or name (optional).
     * @param level           The course level (optional).
     * @param language        The course language (optional).
     * @param skill           The skill substring (optional).
     * @param category        The category substring (optional).
     * @param title           The title substring (optional).
     * @param isAuthenticated Whether the user is authenticated.
     * @return List of matching course responses.
     */
    List<CourseResponse> advancedSearch(String instructor, com.gitittogether.skillForge.server.course.model.utils.Level level, com.gitittogether.skillForge.server.course.model.utils.Language language, String skill, String category, String title);

    /** Delegates to GenAI, then saves & enrolls user before returning */
    CourseResponse generateFromGenAi(LearningPathRequest request);

} 