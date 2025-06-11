package com.gitittogether.skillForge.server.user.mapper.course;

import com.gitittogether.skillForge.server.user.dto.request.course.CategoryRequest;
import com.gitittogether.skillForge.server.user.dto.response.course.CategoryResponse;
import com.gitittogether.skillForge.server.user.model.course.Category;

public class CategoryMapper {

    public static CategoryResponse toCategoryResponse(Category model) {
        if (model == null) return null;
        return CategoryResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .build();
    }

    public static CategoryRequest toCategoryRequest(Category model) {
        if (model == null) return null;
        return CategoryRequest.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .build();
    }

    public static Category requestToCategory(CategoryRequest request) {
        if (request == null) return null;
        return Category.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public static Category responseToCategory(CategoryResponse response) {
        if (response == null) return null;
        return Category.builder()
                .id(response.getId())
                .name(response.getName())
                .description(response.getDescription())
                .build();
    }
}
