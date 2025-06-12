package com.gitittogether.skillforge.server.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SkillForgeApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillForgeApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(
			RouteLocatorBuilder builder,
			@Value("${user.service.uri}") String userServiceUri,
			@Value("${course.service.uri}") String courseServiceUri
	) {
		return builder.routes()
				.route("user-service", r -> r
						.path("/api/v1/users/**")
						.uri(userServiceUri))
				.route("course-service", r -> r
						.path("/api/v1/courses/**")
						.uri(courseServiceUri))
				.build();
	}

	@Bean
	@Profile("dev")
	public CorsWebFilter devCorsWebFilter(@Value("${gateway.cors.allowed-origins}") List<String> allowedOrigins) {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(allowedOrigins);
		System.out.println("Using CORS configuration for development environment: allowing all origins = " + config.getAllowedOrigins());
		return getCorsWebFilter(config);
	}

	@Bean
	@Profile("docker")
	public CorsWebFilter prodCorsWebFilter(@Value("${gateway.cors.allowed-origins}") List<String> allowedOrigins) {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(allowedOrigins);
		System.out.println("Using CORS configuration for production environment: allowed origins = " + allowedOrigins);
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