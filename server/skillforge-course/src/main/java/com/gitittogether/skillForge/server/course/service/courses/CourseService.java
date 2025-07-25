package com.gitittogether.skillForge.server.course.service.courses;

import com.gitittogether.skillForge.server.course.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.course.dto.request.course.CourseUpdateRequest;
import com.gitittogether.skillForge.server.course.dto.request.course.LearningPathRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseSummaryResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.EnrolledUserInfoResponse;
import com.gitittogether.skillForge.server.course.dto.response.utils.EmbedResult;
import com.gitittogether.skillForge.server.course.model.utils.Language;
import com.gitittogether.skillForge.server.course.model.utils.Level;

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
    List<CourseSummaryResponse> getPublishedCourses();

    /**
     * Updates an existing course.
     *
     * @param courseId The ID of the course to update.
     * @param request  The course update request.
     * @return The updated course response.
     */
    CourseResponse updateCourse(String courseId, CourseRequest request);

    /**
     * Updates an existing course partially (without requiring all mandatory fields).
     *
     * @param courseId The ID of the course to update.
     * @param request  The partial course update request.
     * @return The updated course response.
     */
    CourseResponse updateCoursePartial(String courseId, CourseUpdateRequest request);

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
     * @param instructor  The instructor ID or name (optional).
     * @param level       The course level (optional).
     * @param language    The course language (optional).
     * @param skill       The skill substring (optional).
     * @param category    The category substring (optional).
     * @param title       The title substring (optional).
     * @param isPublished Whether to filter by published status (optional).
     * @param isPublic    Whether to filter by public status (optional).
     * @return List of matching course responses.
     */
    List<CourseResponse> advancedSearch(String instructor, Level level, Language language, String skill, String category, String title, Boolean isPublished, Boolean isPublic);


    /**
     * Generates a course from a Learning Path request using Generative AI.
     *
     * @param request The Learning Path request containing the necessary information to generate a course.
     * @return CourseRequest containing the generated course details to be stored in the database.
     */
    CourseRequest generateCourseFromGenAi(LearningPathRequest request, String userId, String authHeader);

    /**
     * Confirms the generation of a course from a Learning Path request.
     * <p>
     * This method is called after the course has been generated and the user has reviewed it.
     * It retrieves the last generated course details, create the course in the database, enroll the user and return the course response.
     *
     * @return CourseResponse containing the confirmed course details.
     */
    CourseResponse confirmCourseGeneration(String userId);

    /**
     * Generates a response to a given Prompt.
     * This is delegated to the GenaAi Service
     *
     * @param prompt The prompt to generate a response for.
     * @return The generated response as a String.
     */
    //This methode can be moved to a separate service //TODO: refactor
    String generateResponseFromGenAi(String prompt);

    /**
     * Crawls the web for course content based on a given URL.
     *
     * @param url The URL to crawl for course content.
     * @return boolean indicating whether the crawling was successful.
     */
    //This methode can be moved to a separate service //TODO: refactor
    EmbedResult crawlWebForCourseContent(String url);

}