package com.gitittogether.skillForge.server.course.controller;

import com.gitittogether.skillForge.server.course.model.course.Course;
import com.gitittogether.skillForge.server.course.model.utils.Level;
import com.gitittogether.skillForge.server.course.repository.course.CourseRepository;
import com.gitittogether.skillForge.server.course.model.utils.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class CourseControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CourseRepository courseRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        courseRepository.deleteAll();
    }

    @Test
    void testGetPublicCourses() throws Exception {
        // Create test courses
        Course publicCourse = Course.builder()
                .title("Public Course")
                .description("A public course for testing")
                .instructor("Test Instructor")
                .isPublic(true)
                .published(true)
                .language(Language.EN)
                .level(Level.BEGINNER)
                .skills(new ArrayList<>())
                .modules(new ArrayList<>())
                .categories(new ArrayList<>())
                .build();

        Course privateCourse = Course.builder()
                .title("Private Course")
                .description("A private course for testing")
                .instructor("Test Instructor")
                .isPublic(false)
                .published(true)
                .language(Language.EN)
                .level(Level.BEGINNER)
                .skills(new ArrayList<>())
                .modules(new ArrayList<>())
                .categories(new ArrayList<>())
                .build();

        courseRepository.save(publicCourse);
        courseRepository.save(privateCourse);

        // Test public courses endpoint
        mockMvc.perform(get("/api/v1/courses/public")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Public Course"))
                .andExpect(jsonPath("$[0].isPublic").value(true))
                .andExpect(jsonPath("$[1]").doesNotExist()); // Should only return public course
    }

    @Test
    void testGetPublicPublishedCourses() throws Exception {
        // Create test courses
        Course publicPublishedCourse = Course.builder()
                .title("Public Published Course")
                .description("A public and published course for testing")
                .instructor("Test Instructor")
                .isPublic(true)
                .published(true)
                .language(Language.EN)
                .level(Level.BEGINNER)
                .skills(new ArrayList<>())
                .modules(new ArrayList<>())
                .categories(new ArrayList<>())
                .build();

        Course publicUnpublishedCourse = Course.builder()
                .title("Public Unpublished Course")
                .description("A public but unpublished course for testing")
                .instructor("Test Instructor")
                .isPublic(true)
                .published(false)
                .language(Language.EN)
                .level(Level.BEGINNER)
                .skills(new ArrayList<>())
                .modules(new ArrayList<>())
                .categories(new ArrayList<>())
                .build();

        courseRepository.save(publicPublishedCourse);
        courseRepository.save(publicUnpublishedCourse);

        // Test public and published courses endpoint
        mockMvc.perform(get("/api/v1/courses/public/published")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Public Published Course"))
                .andExpect(jsonPath("$[0].isPublic").value(true))
                .andExpect(jsonPath("$[0].published").value(true))
                .andExpect(jsonPath("$[1]").doesNotExist()); // Should only return public and published course
    }
} 