package com.gitittogether.skillForge.server.course.mapper.course;

import com.gitittogether.skillForge.server.course.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseSummaryResponse;
import com.gitittogether.skillForge.server.course.model.course.Course;

import java.util.stream.Collectors;

public class CourseMapper {

    public static CourseResponse toCourseResponse(Course model) {
        if (model == null) return null;
        return CourseResponse.builder()
                .id(model.getId())
                .title(model.getTitle())
                .description(model.getDescription())
                .instructor(model.getInstructor())
                .skills(model.getSkills())
                .modules(model.getModules().stream().map(ModuleMapper::toModuleResponse).collect(Collectors.toList()))
                .numberOfEnrolledUsers(model.getNumberOfEnrolledUsers())
                .categories(model.getCategories())
                .level(model.getLevel())
                .thumbnailUrl(model.getThumbnailUrl())
                .published(model.getPublished())
                .isPublic(model.getIsPublic())
                .language(model.getLanguage())
                .rating(model.getRating())
                .enrolledUsers(model.getEnrolledUsers())
                .build();
    }

    public static CourseRequest toCourseRequest(Course model) {
        if (model == null) return null;
        return CourseRequest.builder()
                .id(model.getId())
                .title(model.getTitle())
                .description(model.getDescription())
                .instructor(model.getInstructor())
                .skills(model.getSkills())
                .modules(model.getModules().stream().map(ModuleMapper::toModuleRequest).collect(Collectors.toList()))
                .numberOfEnrolledUsers(model.getNumberOfEnrolledUsers())
                .categories(model.getCategories())
                .level(model.getLevel())
                .thumbnailUrl(model.getThumbnailUrl())
                .published(model.getPublished())
                .isPublic(model.getIsPublic())
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
                .skills(request.getSkills())
                .modules(request.getModules().stream().map(ModuleMapper::requestToModule).collect(Collectors.toList()))
                .numberOfEnrolledUsers(request.getNumberOfEnrolledUsers())
                .categories(request.getCategories())
                .level(request.getLevel())
                .thumbnailUrl(request.getThumbnailUrl())
                .published(request.getPublished())
                .isPublic(request.getIsPublic())
                .language(request.getLanguage())
                .rating(request.getRating())
                .enrolledUsers(request.getEnrolledUsers().stream().map(EnrolledUserInfoMapper::requestToEnrolledUserInfo).collect(Collectors.toList()))
                .build();
    }

    public static Course responseToCourse(CourseResponse response) {
        if (response == null) return null;
        return Course.builder()
                .id(response.getId())
                .title(response.getTitle())
                .description(response.getDescription())
                .instructor(response.getInstructor())
                .skills(response.getSkills())
                .modules(response.getModules().stream().map(ModuleMapper::responseToModule).collect(Collectors.toList()))
                .numberOfEnrolledUsers(response.getNumberOfEnrolledUsers())
                .categories(response.getCategories())
                .level(response.getLevel())
                .thumbnailUrl(response.getThumbnailUrl())
                .published(response.getPublished())
                .isPublic(response.getIsPublic())
                .language(response.getLanguage())
                .rating(response.getRating())
                .enrolledUsers(response.getEnrolledUsers())
                .build();
    }

    public static CourseSummaryResponse toCourseSummaryResponse(Course model) {
        if (model == null) return null;
        return CourseSummaryResponse.builder()
                .id(model.getId())
                .title(model.getTitle())
                .description(model.getDescription())
                .instructor(model.getInstructor())
                .skills(model.getSkills())
                .thumbnailUrl(model.getThumbnailUrl())
                .numberOfEnrolledUsers(model.getNumberOfEnrolledUsers())
                .categories(model.getCategories())
                .level(model.getLevel())
                .isPublic(model.getIsPublic())
                .published(model.getPublished())
                .language(model.getLanguage())
                .rating(model.getRating())
                .enrolledUsers(model.getEnrolledUsers())
                .build();
    }

}
