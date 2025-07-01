package com.gitittogether.skillForge.server.course.service.courses;

import com.gitittogether.skillForge.server.course.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.EnrolledCourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseSummaryResponse;

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
     * Updates an existing course.
     *
     * @param courseId The ID of the course to update.
     * @param request The course update request.
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
     * @param userId The ID of the user to enroll.
     * @return The enrolled course response.
     */
    EnrolledCourseResponse enrollUserInCourse(String courseId, String userId);

    /**
     * Unenrolls a user from a course.
     *
     * @param courseId The ID of the course to unenroll from.
     * @param userId The ID of the user to unenroll.
     */
    void unenrollUserFromCourse(String courseId, String userId);

    /**
     * Marks a course as completed for a user.
     *
     * @param courseId The ID of the course to complete.
     * @param userId The ID of the user.
     */
    void completeCourseForUser(String courseId, String userId);

    /**
     * Retrieves all courses a user is enrolled in.
     *
     * @param userId The ID of the user.
     * @return List of enrolled course responses.
     */
    List<EnrolledCourseResponse> getUserEnrolledCourses(String userId);

    /**
     * Bookmarks a course for a user.
     *
     * @param courseId The ID of the course to bookmark.
     * @param userId The ID of the user.
     */
    void bookmarkCourse(String courseId, String userId);

    /**
     * Unbookmarks a course for a user.
     *
     * @param courseId The ID of the course to unbookmark.
     * @param userId The ID of the user.
     */
    void unbookmarkCourse(String courseId, String userId);
} 