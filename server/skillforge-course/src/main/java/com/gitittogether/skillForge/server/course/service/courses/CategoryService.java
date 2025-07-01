package com.gitittogether.skillForge.server.course.service.courses;

import com.gitittogether.skillForge.server.course.dto.request.course.CategoryRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CategoryResponse;

import java.util.List;

public interface CategoryService {
    
    /**
     * Creates a new category.
     *
     * @param request The category creation request.
     * @return The created category response.
     */
    CategoryResponse createCategory(CategoryRequest request);

    /**
     * Retrieves a category by its ID.
     *
     * @param categoryId The ID of the category to retrieve.
     * @return The category response.
     */
    CategoryResponse getCategory(String categoryId);

    /**
     * Retrieves all categories.
     *
     * @return List of all category responses.
     */
    List<CategoryResponse> getAllCategories();

    /**
     * Updates an existing category.
     *
     * @param categoryId The ID of the category to update.
     * @param request The category update request.
     * @return The updated category response.
     */
    CategoryResponse updateCategory(String categoryId, CategoryRequest request);

    /**
     * Deletes a category.
     *
     * @param categoryId The ID of the category to delete.
     */
    void deleteCategory(String categoryId);

    /**
     * Finds a category by name.
     *
     * @param name The category name.
     * @return The category response.
     */
    CategoryResponse getCategoryByName(String name);

    /**
     * Searches categories by name.
     *
     * @param name The partial name to search for.
     * @return List of matching category responses.
     */
    List<CategoryResponse> searchCategoriesByName(String name);
} 