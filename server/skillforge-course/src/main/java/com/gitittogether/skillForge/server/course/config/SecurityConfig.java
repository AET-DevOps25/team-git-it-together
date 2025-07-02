package com.gitittogether.skillForge.server.course.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    /**
     * Configures the security filter chain for the course service.
     * This service is designed to be called only through the API gateway,
     * so we configure it to expect JWT authentication for most endpoints
     * while allowing public access to health checks and public course endpoints.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless API
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints: health checks, public courses, Swagger/OpenAPI
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses/categories/**").permitAll()
                        .requestMatchers(
                                "/actuator/*",
                                "/api/v1/courses/health",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // All other course endpoints require authentication (JWT from gateway)
                        .requestMatchers("/api/v1/courses/**").authenticated()
                        // Block all other requests
                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
