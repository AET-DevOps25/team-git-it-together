package com.gitittogether.skillforge.server.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(
            RouteLocatorBuilder builder,
            JwtAuthenticationFilter filter,
            @Value("${user.service.uri}") String userServiceUri,
            @Value("${course.service.uri}") String courseServiceUri
    ) {
        log.info("GatewayConfig: Configuring routes with user service URI: {} and course service URI: {}", 
                userServiceUri, courseServiceUri);
        
        RouteLocator routeLocator = builder.routes()
                .route("user-service", r -> r.path("/api/v1/users/**")
                        .filters(f -> f.filter(filter))
                        .uri(userServiceUri))
                .route("course-service", r -> r.path("/api/v1/courses/**")
                        .filters(f -> f.filter(filter))
                        .uri(courseServiceUri))
                .build();
        
        log.info("GatewayConfig: Routes configured successfully");
        return routeLocator;
    }
}
