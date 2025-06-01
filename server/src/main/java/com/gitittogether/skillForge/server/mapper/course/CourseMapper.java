package com.gitittogether.skillForge.server.mapper;

import com.gitittogether.skillForge.server.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.model.courses.Course;

import java.util.stream.Collectors;

public class CourseMapper {
    public static CourseResponse toCourseResponse(Course model) {
        if (model == null) return null;
        return CourseResponse.builder()
                .id(model.getId())
                .title(model.getTitle())
                .description(model.getDescription())
                .instructor(model.getInstructor())
                .skills(model.getSkills().stream().map(SkillMapper::toSkillResponse).collect(Collectors.toList()))
                .modules(model.getModules().stream().map(ModuleMapper::toModuleResponse).collect(Collectors.toList()))
                .numberOfEnrolledUsers(model.getNumberOfEnrolledUsers())
                .categories(model.getCategories().stream().map(CategoryMapper::toCategoryResponse).collect(Collectors.toList()))
                .level(model.getLevel())
                .thumbnailUrl(model.getThumbnailUrl())
                .published(model.isPublished())
                .language(model.getLanguage())
                .rating(model.getRating())
                .build();
    }

    }
