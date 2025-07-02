package com.gitittogether.skillForge.server.course.repository.course;

import com.gitittogether.skillForge.server.course.model.course.Course;
import com.gitittogether.skillForge.server.course.model.utils.Level;
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
    List<Course> findByLevel(Level level);

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
    List<Course> findBySkillsContainingIgnoreCase(String skillName);

    /**
     * Find by category name fuzzy.
     *
     * @param categoryName The category name.
     * @return List of courses containing the category name.
     */
    List<Course> findByCategoriesContainingIgnoreCase(String categoryName);

    /**
     * Find courses by title.
     *
     * @param title The course title.
     * @return List of courses with the specified title.
     */
    List<Course> findByTitle(String title);

    /**
     * Find Course by title fuzzy.
     *
     * @param title The course title.
     * @return List of courses with the specified title.
     */
    List<Course> findByTitleContainingIgnoreCase(String title);


} 