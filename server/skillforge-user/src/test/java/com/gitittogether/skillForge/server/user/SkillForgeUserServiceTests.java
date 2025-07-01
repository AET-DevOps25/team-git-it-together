package com.gitittogether.skillForge.server.user;

import com.gitittogether.skillForge.server.user.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;

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
    "jwt.secret=test-jwt-secret-key-for-testing-only",
    "jwt.expirationMs=3600000",
    "logging.level.root=WARN",
    "logging.level.com.gitittogether.skillForge.server.user=WARN"
})
@Import(TestConfig.class)
class SkillForgeUserServiceTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads successfully
        // with minimal configuration for faster execution
    }

}
