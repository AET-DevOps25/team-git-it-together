package com.gitittogether.skillForge.server.course.repository.course;

import com.gitittogether.skillForge.server.course.model.course.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    
    /**
     * Find all public courses for landing page display.
     *
     * @return List of public courses.
     */
    List<Course> findByIsPublicTrue();
    
    /**
     * Find all published courses.
     *
     * @return List of published courses.
     */
    List<Course> findByPublishedTrue();
    
    /**
     * Find all public and published courses.
     *
     * @return List of public and published courses.
     */
    List<Course> findByIsPublicTrueAndPublishedTrue();
    
    /**
     * Find courses by instructor.
     *
     * @param instructor The instructor ID or name.
     * @return List of courses by the instructor.
     */
    List<Course> findByInstructor(String instructor);
    
    /**
     * Find courses by level.
     *
     * @param level The course level.
     * @return List of courses with the specified level.
     */
    List<Course> findByLevel(com.gitittogether.skillForge.server.course.model.course.Level level);
    
    /**
     * Find courses by language.
     *
     * @param language The course language.
     * @return List of courses with the specified language.
     */
    List<Course> findByLanguage(com.gitittogether.skillForge.server.course.model.utils.Language language);
    
    /**
     * Find courses that contain a specific skill.
     *
     * @param skillName The skill name.
     * @return List of courses containing the skill.
     */
    List<Course> findBySkillsContaining(String skillName);
    
    /**
     * Find courses that contain a specific category.
     *
     * @param categoryName The category name.
     * @return List of courses containing the category.
     */
    List<Course> findByCategoriesContaining(String categoryName);
} 