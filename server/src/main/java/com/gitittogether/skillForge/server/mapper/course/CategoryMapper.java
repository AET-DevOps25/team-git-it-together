package com.gitittogether.skillForge.server.mapper;

import com.gitittogether.skillForge.server.dto.response.course.CategoryResponse;
import com.gitittogether.skillForge.server.model.courses.Category;

public class CategoryMapper {

    public static CategoryResponse toCategoryResponse(Category model) {
        if(model == null) return null;
        return CategoryResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .build();
    }
}
