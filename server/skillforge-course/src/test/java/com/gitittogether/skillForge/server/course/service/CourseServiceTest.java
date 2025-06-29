package com.gitittogether.skillForge.server.course.service;

import com.gitittogether.skillForge.server.course.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.course.model.course.Course;
import com.gitittogether.skillForge.server.course.model.course.Level;
import com.gitittogether.skillForge.server.course.model.utils.Language;
import com.gitittogether.skillForge.server.course.repository.course.CourseRepository;
import com.gitittogether.skillForge.server.course.service.courses.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private com.gitittogether.skillForge.server.course.repository.course.UserCourseRepository userCourseRepository;

    @Mock
    private com.gitittogether.skillForge.server.course.repository.course.UserBookmarkRepository userBookmarkRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course publicCourse;
    private Course privateCourse;
    private Course publicPublishedCourse;

    @BeforeEach
    void setUp() {
        publicCourse = Course.builder()
                .id("public-course-id")
                .title("Public Course")
                .description("A public course")
                .instructor("Test Instructor")
                .isPublic(true)
                .published(false)
                .language(Language.EN)
                .level(Level.BEGINNER)
                .skills(new ArrayList<>())
                .modules(new ArrayList<>())
                .categories(new ArrayList<>())
                .build();

        privateCourse = Course.builder()
                .id("private-course-id")
                .title("Private Course")
                .description("A private course")
                .instructor("Test Instructor")
                .isPublic(false)
                .published(true)
                .language(Language.EN)
                .level(Level.BEGINNER)
                .skills(new ArrayList<>())
                .modules(new ArrayList<>())
                .categories(new ArrayList<>())
                .build();

        publicPublishedCourse = Course.builder()
                .id("public-published-course-id")
                .title("Public Published Course")
                .description("A public and published course")
                .instructor("Test Instructor")
                .isPublic(true)
                .published(true)
                .language(Language.EN)
                .level(Level.BEGINNER)
                .skills(new ArrayList<>())
                .modules(new ArrayList<>())
                .categories(new ArrayList<>())
                .build();
    }

    @Test
    void testGetPublicCourses() {
        // Given
        when(courseRepository.findByIsPublicTrue()).thenReturn(Arrays.asList(publicCourse, publicPublishedCourse));

        // When
        List<CourseResponse> result = courseService.getPublicCourses();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(CourseResponse::isPublic));
        verify(courseRepository).findByIsPublicTrue();
    }

    @Test
    void testGetPublicPublishedCourses() {
        // Given
        when(courseRepository.findByIsPublicTrueAndPublishedTrue()).thenReturn(Arrays.asList(publicPublishedCourse));

        // When
        List<CourseResponse> result = courseService.getPublicPublishedCourses();

        // Then
        assertEquals(1, result.size());
        CourseResponse course = result.get(0);
        assertTrue(course.isPublic());
        assertTrue(course.isPublished());
        assertEquals("Public Published Course", course.getTitle());
        verify(courseRepository).findByIsPublicTrueAndPublishedTrue();
    }

    @Test
    void testCreateCourse() {
        // Given
        CourseRequest request = CourseRequest.builder()
                .title("New Course")
                .description("A new course")
                .instructor("Test Instructor")
                .isPublic(true)
                .published(false)
                .language(Language.EN)
                .level(Level.BEGINNER)
                .skills(new ArrayList<>())
                .modules(new ArrayList<>())
                .categories(new ArrayList<>())
                .build();

        Course savedCourse = Course.builder()
                .id("new-course-id")
                .title("New Course")
                .description("A new course")
                .instructor("Test Instructor")
                .isPublic(true)
                .published(false)
                .language(Language.EN)
                .level(Level.BEGINNER)
                .skills(new ArrayList<>())
                .modules(new ArrayList<>())
                .categories(new ArrayList<>())
                .build();

        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // When
        CourseResponse result = courseService.createCourse(request);

        // Then
        assertNotNull(result);
        assertEquals("New Course", result.getTitle());
        assertTrue(result.isPublic());
        assertFalse(result.isPublished());
        verify(courseRepository).save(any(Course.class));
    }
} 