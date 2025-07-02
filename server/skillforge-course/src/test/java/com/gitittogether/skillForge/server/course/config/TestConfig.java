package com.gitittogether.skillForge.server.course.config;

import com.gitittogether.skillForge.server.course.repository.course.CourseRepository;
import com.gitittogether.skillForge.server.course.service.courses.CourseService;
import com.gitittogether.skillForge.server.course.service.courses.CourseServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CourseService courseService(CourseRepository courseRepository) {
        return new CourseServiceImpl(courseRepository);
    }
} 