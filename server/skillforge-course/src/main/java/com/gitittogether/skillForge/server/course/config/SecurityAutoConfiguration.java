package com.gitittogether.skillForge.server.course.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to disable Spring Security's auto-configuration
 * that creates an in-memory user details service with a default password.
 * This prevents the "Using generated security password" warning.
 */
@Configuration
@EnableAutoConfiguration(exclude = {UserDetailsServiceAutoConfiguration.class})
public class SecurityAutoConfiguration {
} 