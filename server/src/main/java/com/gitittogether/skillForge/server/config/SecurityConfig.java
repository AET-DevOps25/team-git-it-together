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
     * Defines a PasswordEncoder bean for encoding passwords.
     * This uses BCrypt, which is a strong hashing algorithm.
     * * @return a PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for our application.
     * This includes:
     * 1) Disabling CSRF protection (not needed for stateless APIs).
     * 2) Setting session management to stateless (we use JWT).
     * 3) Configuring public and protected endpoints.
     * 4) Adding the JWT filter to the chain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // 2) We’re stateless (no HTTP session); we rely on JWT in cookies
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 3) Configure which endpoints are public and which are protected
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