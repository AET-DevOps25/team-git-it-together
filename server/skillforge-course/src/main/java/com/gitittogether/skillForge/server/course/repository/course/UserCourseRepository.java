package com.gitittogether.skillForge.server.course.repository.course;

import com.gitittogether.skillForge.server.course.model.course.EnrolledCourse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCourseRepository extends MongoRepository<EnrolledCourse, String> {
    
    /**
     * Find all enrolled courses for a user.
     *
     * @param userId The user ID.
     * @return List of enrolled courses for the user.
     */
    List<EnrolledCourse> findByProgressUserId(String userId);
    
    /**
     * Find a specific enrolled course for a user.
     *
     * @param courseId The course ID.
     * @param userId The user ID.
     * @return Optional of the enrolled course.
     */
    Optional<EnrolledCourse> findByCourseIdAndProgressUserId(String courseId, String userId);
    
    /**
     * Check if a user is enrolled in a specific course.
     *
     * @param courseId The course ID.
     * @param userId The user ID.
     * @return True if the user is enrolled, false otherwise.
     */
    boolean existsByCourseIdAndProgressUserId(String courseId, String userId);
    
    /**
     * Find all enrolled courses for a specific course.
     *
     * @param courseId The course ID.
     * @return List of enrolled courses for the course.
     */
    @Query("{'course.id': ?0}")
    List<EnrolledCourse> findByCourseId(String courseId);
    
    /**
     * Find all completed courses for a user.
     *
     * @param userId The user ID.
     * @return List of completed courses for the user.
     */
    List<EnrolledCourse> findByProgressUserIdAndProgressCompletedTrue(String userId);
    
    /**
     * Find all in-progress courses for a user.
     *
     * @param userId The user ID.
     * @return List of in-progress courses for the user.
     */
    List<EnrolledCourse> findByProgressUserIdAndProgressCompletedFalse(String userId);
    
    /**
     * Delete all enrolled courses for a user.
     *
     * @param userId The user ID.
     */
    void deleteByProgressUserId(String userId);
    
    /**
     * Delete a specific enrolled course for a user.
     *
     * @param courseId The course ID.
     * @param userId The user ID.
     */
    void deleteByCourseIdAndProgressUserId(String courseId, String userId);
    
    /**
     * Delete all enrolled courses for a specific course.
     *
     * @param courseId The course ID.
     */
    @Query(value = "{'course.id': ?0}", delete = true)
    void deleteByCourseId(String courseId);
} 