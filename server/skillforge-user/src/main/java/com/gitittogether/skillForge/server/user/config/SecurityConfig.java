package com.gitittogether.skillForge.server.user.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.function.Supplier;


@Configuration
@RequiredArgsConstructor
@Profile("!test")
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
                .csrf(AbstractHttpConfigurer::disable) // Disables CSRF (Cross-Site Request Forgery) protection, as it is not needed for stateless APIs.
                .sessionManagement(sm -> sm
                        // Configures session management to be stateless, ensuring that the application does not store session data on the server.
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints: user registration, login, health checks, Swagger/OpenAPI
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll()
                        // docs endpoints
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user-openapi.yaml").permitAll()
                        .requestMatchers(
                                "/api/v1/users/docs/**",
                                "/docs/**",
                                "/docs",
                                "/actuator/*",
                                "/api/v1/users/health",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/docs/user-openapi.yaml",
                                "/user-openapi.yaml",
                                "/api/v1/users/user-openapi.yaml"
                        ).permitAll()
                        // Inter-service communication endpoints (only from course service)
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/*/enroll/*").access(this::isInternalService)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/*/enroll/*").access(this::isInternalService)
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/*/bookmark/*").access(this::isInternalService)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/*/bookmark/*").access(this::isInternalService)
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/*/complete/*").access(this::isInternalService)
                        // All other endpoints require authentication
                        .requestMatchers("/api/v1/users/**").authenticated()
                        .anyRequest().denyAll()
                )
                .exceptionHandling(eh -> eh.
                        authenticationEntryPoint(jwtAuthEntryPoint)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private AuthorizationDecision isInternalService(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        String serviceKey = context.getRequest().getHeader("X-Service-Key");
        boolean hasValidServiceKey = "course-service-key".equals(serviceKey);
        return new AuthorizationDecision(hasValidServiceKey);
    }
}