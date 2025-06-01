package com.gitittogether.skillForge.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;


    /**
     * Defines a PasswordEncoder bean so that we can inject it anywhere.
     * We use BCryptPasswordEncoder for hashing passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Main security filter configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1) Enable CSRF, store CSRF token in a cookie (HttpOnly = false so JS can read if needed)
                .csrf(AbstractHttpConfigurer::disable) // <-- this line is crucial!
                // 2) We’re stateless (no HTTP session); we rely on JWT in cookies
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 3) Configure which endpoints are public vs. protected
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints: user registration, login, health checks, Swagger/OpenAPI
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses/public/**").permitAll()
                        .requestMatchers(
                                "/actuator/*",
                                "/api/v1/health",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // All other endpoints require authentication
                        .requestMatchers("/api/v1/users/**").authenticated()
                        .requestMatchers("/api/v1/courses/**").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(eh -> eh.
                        authenticationEntryPoint(jwtAuthEntryPoint)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}