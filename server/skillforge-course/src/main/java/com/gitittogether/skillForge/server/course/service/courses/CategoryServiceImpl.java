package com.gitittogether.skillForge.server.course.service.courses;

import com.gitittogether.skillForge.server.course.dto.request.course.CategoryRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CategoryResponse;
import com.gitittogether.skillForge.server.course.exception.ResourceNotFoundException;
import com.gitittogether.skillForge.server.course.mapper.course.CategoryMapper;
import com.gitittogether.skillForge.server.course.model.course.Category;
import com.gitittogether.skillForge.server.course.repository.course.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating new category: {}", request.getName());
        
        // Check if category already exists
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
        }
        
        Category category = CategoryMapper.requestToCategory(request);
        Category savedCategory = categoryRepository.save(category);
        
        log.info("Created category with ID: {}", savedCategory.getId());
        return CategoryMapper.toCategoryResponse(savedCategory);
    }

    @Override
    public CategoryResponse getCategory(String categoryId) {
        log.info("Fetching category: {}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
        
        return CategoryMapper.toCategoryResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        log.info("Fetching all categories");
        
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(CategoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(String categoryId, CategoryRequest request) {
        log.info("Updating category: {}", categoryId);
        
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
        
        // Check if the new name conflicts with another category
        if (!existingCategory.getName().equals(request.getName()) && 
            categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
        }
        
        Category updatedCategory = CategoryMapper.requestToCategory(request);
        updatedCategory.setId(categoryId);
        Category savedCategory = categoryRepository.save(updatedCategory);
        
        log.info("Updated category with ID: {}", savedCategory.getId());
        return CategoryMapper.toCategoryResponse(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(String categoryId) {
        log.info("Deleting category: {}", categoryId);
        
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with ID: " + categoryId);
        }
        
        categoryRepository.deleteById(categoryId);
        log.info("Deleted category with ID: {}", categoryId);
    }

    @Override
    public CategoryResponse getCategoryByName(String name) {
        log.info("Fetching category by name: {}", name);
        
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + name));
        
        return CategoryMapper.toCategoryResponse(category);
    }

    @Override
    public List<CategoryResponse> searchCategoriesByName(String name) {
        log.info("Searching categories by name: {}", name);
        
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(name);
        return categories.stream()
                .map(CategoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }
} 