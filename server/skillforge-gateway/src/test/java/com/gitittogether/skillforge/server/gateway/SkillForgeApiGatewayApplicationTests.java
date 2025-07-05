package com.gitittogether.skillforge.server.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
        "spring.autoconfigure.exclude=org.springframework.cloud.gateway.config.GatewayAutoConfiguration",
        "spring.autoconfigure.exclude=org.springframework.cloud.gateway.config.GatewayMetricsAutoConfiguration",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration"
    }
)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379",
    "spring.data.redis.password=",
    "spring.security.enabled=false",
    "jwt.secret=test-jwt-secret-key-for-testing-only",
    "jwt.expirationMs=3600000",
    "user.service.uri=http://localhost:8082",
    "course.service.uri=http://localhost:8083",
    "gateway.health.uri=http://localhost:8081",
    "logging.level.root=WARN",
    "logging.level.com.gitittogether.skillforge.server.gateway=WARN"
})
class SkillForgeApiGatewayApplicationTests {

	@Test
	void contextLoads() {
		// This test verifies that the Spring context loads successfully
		// with minimal configuration for faster execution
	}

}
