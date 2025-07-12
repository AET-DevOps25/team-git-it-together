package com.gitittogether.skillforge.server.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    @Profile("dev")
    public CorsWebFilter devCorsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // Use addAllowedOriginPattern to avoid multiple headers
        config.addAllowedOriginPattern("*");
        System.out.println("Using CORS configuration for development environment: allowing all origins");
        return getCorsWebFilter(config);
    }

    @Bean
    @Profile("docker")
    public CorsWebFilter dockerCorsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // Use addAllowedOriginPattern to avoid multiple headers
        config.addAllowedOriginPattern("*");
        System.out.println("Using CORS configuration for Docker environment: allowing all origins");
        return getCorsWebFilter(config);
    }

    @Bean
    @Profile("prod")
    public CorsWebFilter prodCorsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // Use addAllowedOriginPattern to avoid multiple headers
        config.addAllowedOriginPattern("*");
        System.out.println("Using CORS configuration for production environment: allowing all origins");
        return getCorsWebFilter(config);
    }

    private CorsWebFilter getCorsWebFilter(CorsConfiguration config) {
        config.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}