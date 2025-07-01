package com.gitittogether.skillForge.server.course;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.actuator.ActuatorAutoConfiguration",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.metrics.export.prometheus.PrometheusMetricsExportAutoConfiguration"
    }
)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.data.mongodb.enabled=false",
    "spring.security.enabled=false",
    "logging.level.root=WARN",
    "logging.level.com.gitittogether.skillForge.server.course=WARN"
})
class SkillForgeCourseServiceTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads successfully
        // with minimal configuration for faster execution
    }

}
