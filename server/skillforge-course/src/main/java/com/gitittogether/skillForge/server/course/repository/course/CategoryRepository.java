package com.gitittogether.skillForge.server.course.repository.course;

import com.gitittogether.skillForge.server.course.model.course.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    
    /**
     * Find a category by name.
     *
     * @param name The category name.
     * @return Optional of the category.
     */
    Optional<Category> findByName(String name);
    
    /**
     * Check if a category exists by name.
     *
     * @param name The category name.
     * @return True if the category exists, false otherwise.
     */
    boolean existsByName(String name);
    
    /**
     * Find all categories by name containing the given string (case-insensitive).
     *
     * @param name The partial name to search for.
     * @return List of categories matching the search criteria.
     */
    List<Category> findByNameContainingIgnoreCase(String name);
    
    /**
     * Delete a category by name.
     *
     * @param name The category name.
     */
    void deleteByName(String name);
} 