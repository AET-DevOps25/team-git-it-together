package com.gitittogether.skillForge.server.user.config;

/*
 * CORS configuration for the SkillForge server.
 * This configuration allows cross-origin requests from any origin,
 * supports common HTTP methods, and allows all headers.
 * * It is used to enable CORS for the entire application,
 * allowing it to handle requests from different origins.
 * * This is particularly useful for frontend applications that need to interact with the backend API,
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        // Use same configuration as gateway to avoid conflicts
        cfg.addAllowedOriginPattern("*");
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(false);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}