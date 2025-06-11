package com.gitittogether.skillForge.server.user.mapper.course;

import com.gitittogether.skillForge.server.user.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.user.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.user.mapper.skill.SkillMapper;
import com.gitittogether.skillForge.server.user.model.course.Course;
import com.gitittogether.skillForge.server.user.model.course.CourseProgress;
import com.gitittogether.skillForge.server.user.model.course.EnrolledCourse;

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

    public static CourseRequest toCourseRequest(Course model) {
        if (model == null) return null;
        return CourseRequest.builder()
                .id(model.getId())
                .title(model.getTitle())
                .description(model.getDescription())
                .instructor(model.getInstructor())
                .skills(model.getSkills().stream().map(SkillMapper::toSkillRequest).collect(Collectors.toList()))
                .modules(model.getModules().stream().map(ModuleMapper::toModuleRequest).collect(Collectors.toList()))
                .numberOfEnrolledUsers(model.getNumberOfEnrolledUsers())
                .categories(model.getCategories().stream().map(CategoryMapper::toCategoryRequest).collect(Collectors.toList()))
                .level(model.getLevel())
                .thumbnailUrl(model.getThumbnailUrl())
                .published(model.isPublished())
                .language(model.getLanguage())
                .rating(model.getRating())
                .build();
    }

    public static Course requestToCourse(CourseRequest request) {
        if (request == null) return null;
        return Course.builder()
                .id(request.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .instructor(request.getInstructor())
                .skills(request.getSkills().stream().map(SkillMapper::requestToSkill).collect(Collectors.toList()))
                .modules(request.getModules().stream().map(ModuleMapper::requestToModule).collect(Collectors.toList()))
                .numberOfEnrolledUsers(request.getNumberOfEnrolledUsers())
                .categories(request.getCategories().stream().map(CategoryMapper::requestToCategory).collect(Collectors.toList()))
                .level(request.getLevel())
                .thumbnailUrl(request.getThumbnailUrl())
                .published(request.isPublished())
                .language(request.getLanguage())
                .rating(request.getRating())
                .build();
    }

    public static Course responseToCourse(CourseResponse response) {
        if (response == null) return null;
        return Course.builder()
                .id(response.getId())
                .title(response.getTitle())
                .description(response.getDescription())
                .instructor(response.getInstructor())
                .skills(response.getSkills().stream().map(SkillMapper::responseToSkill).collect(Collectors.toList()))
                .modules(response.getModules().stream().map(ModuleMapper::responseToModule).collect(Collectors.toList()))
                .numberOfEnrolledUsers(response.getNumberOfEnrolledUsers())
                .categories(response.getCategories().stream().map(CategoryMapper::responseToCategory).collect(Collectors.toList()))
                .level(response.getLevel())
                .thumbnailUrl(response.getThumbnailUrl())
                .published(response.isPublished())
                .language(response.getLanguage())
                .rating(response.getRating())
                .build();
    }

    public static EnrolledCourse toNewEnrolledCourse(CourseResponse response, String userId) {
        if (response == null) return null;
        return EnrolledCourse.builder()
                .course(responseToCourse(response))
                .progress(CourseProgress.builder()
                        .courseId(response.getId())
                        .userId(userId)
                        .build())
                .build();
    }


}
