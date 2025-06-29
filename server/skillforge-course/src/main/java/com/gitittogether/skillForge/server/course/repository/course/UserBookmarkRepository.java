package com.gitittogether.skillForge.server.course.repository.course;

import com.gitittogether.skillForge.server.course.model.course.UserBookmark;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookmarkRepository extends MongoRepository<UserBookmark, String> {
    
    /**
     * Find all bookmarked courses for a user.
     *
     * @param userId The user ID.
     * @return List of user bookmarks.
     */
    List<UserBookmark> findByUserId(String userId);
    
    /**
     * Find a specific bookmark for a user and course.
     *
     * @param userId The user ID.
     * @param courseId The course ID.
     * @return Optional of the user bookmark.
     */
    Optional<UserBookmark> findByUserIdAndCourseId(String userId, String courseId);
    
    /**
     * Check if a user has bookmarked a specific course.
     *
     * @param userId The user ID.
     * @param courseId The course ID.
     * @return True if the course is bookmarked, false otherwise.
     */
    boolean existsByUserIdAndCourseId(String userId, String courseId);
    
    /**
     * Delete all bookmarks for a user.
     *
     * @param userId The user ID.
     */
    void deleteByUserId(String userId);
    
    /**
     * Delete a specific bookmark for a user and course.
     *
     * @param userId The user ID.
     * @param courseId The course ID.
     */
    void deleteByUserIdAndCourseId(String userId, String courseId);
    
    /**
     * Delete all bookmarks for a course.
     *
     * @param courseId The course ID.
     */
    void deleteByCourseId(String courseId);
} 