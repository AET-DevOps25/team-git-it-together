package com.gitittogether.skillforge.server.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

@Slf4j
@Configuration
@Profile("!test")
public class GatewayConfig {

    @Bean
    public RouteLocator routes(
            RouteLocatorBuilder builder,
            JwtAuthenticationFilter jwtFilter,
            RedisRateLimiter redisRateLimiter,
            KeyResolver userKeyResolver,
            @Value("${user.service.uri}") String userServiceUri,
            @Value("${course.service.uri}") String courseServiceUri,
            @Value("${gateway.health.uri}") String gatewayHealthUri
    ) {
        log.info("GatewayConfig: Configuring routes with user service URI: {} and course service URI: {}",
                userServiceUri, courseServiceUri);

        RouteLocator routeLocator = builder.routes()
                // Health check routes (no rate limiting, no auth) - must come first
                .route("gateway-health", r -> r.path("/actuator/health")
                        .uri(gatewayHealthUri))
                .route("user-health", r -> r.path("/api/v1/users/health")
                        .uri(userServiceUri))
                .route("course-health", r -> r.path("/api/v1/courses/health")
                        .uri(courseServiceUri))

                // Swagger documentation routes (no auth, no rate limiting) - MUST come before general routes
                .route("user-service-docs", r -> r.path("/api/v1/users/docs", "/api/v1/users/docs/**")
                        .uri(userServiceUri))
                .route("course-service-docs", r -> r.path("/api/v1/courses/docs", "/api/v1/courses/docs/**")
                        .uri(courseServiceUri))

                // User service routes that are public (no auth)
                .route("user-service-auth", r -> r.path("/api/v1/users/login", "/api/v1/users/register")
                        .filters(f -> f
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
                                        .setDenyEmptyKey(false)
                                        .setEmptyKeyStatus("TOO_MANY_REQUESTS")))
                        .uri(userServiceUri))

                // The Public course service routes (no JWT required)
                .route("course-service-public", r -> r.path("/api/v1/courses/public/**")
                        .filters(f -> f
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
                                        .setDenyEmptyKey(false)
                                        .setEmptyKeyStatus("TOO_MANY_REQUESTS")))
                        .uri(courseServiceUri))

                // Protected user service routes (requires JWT)
                .route("user-service-protected", r -> r.path("/api/v1/users/**")
                        .filters(f -> f
                                .filter(jwtFilter)
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
                                        .setDenyEmptyKey(false)
                                        .setEmptyKeyStatus("TOO_MANY_REQUESTS")))
                        .uri(userServiceUri))

                // Protected course service routes (requires JWT)
                .route("course-service-protected", r -> r.path("/api/v1/courses/**")
                        .filters(f -> f
                                .filter(jwtFilter)
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
                                        .setDenyEmptyKey(false)
                                        .setEmptyKeyStatus("TOO_MANY_REQUESTS")))
                        .uri(courseServiceUri))

                // Chat service routes (requires JWT) - must come before course-service-protected to avoid conflicts
                .route("chat-service-protected", r -> r.path("/api/v1/chat/**")
                        .filters(f -> f
                                .filter(jwtFilter)
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
                                        .setDenyEmptyKey(false)
                                        .setEmptyKeyStatus("TOO_MANY_REQUESTS")))
                        .uri(courseServiceUri))
                .build();

        log.info("GatewayConfig: Routes configured successfully with Redis-based rate limiting and JWT authentication");
        return routeLocator;
    }
}
