package com.gitittogether.skillforge.server.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JwtConfig implements CommandLineRunner {

    private final JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== JWT Configuration Validation ===");
        log.info("JWT Secret Length: {}", jwtSecret != null ? jwtSecret.length() : "NULL");
        log.info("Using Default Secret: {}", isUsingDefaultSecret());
        log.info("Environment Variable JWT_SECRET: {}", System.getenv("JWT_SECRET") != null ? "SET" : "NOT SET");
        
        if (isUsingDefaultSecret()) {
            log.warn("⚠️  WARNING: Using default JWT secret. This should only be used in development!");
            log.warn("⚠️  Set JWT_SECRET environment variable for production use.");
            log.warn("⚠️  Ensure the same JWT_SECRET is used across all microservices!");
        }
        
        // Initialize JWT utility logging
        jwtUtil.initialize();
        
        log.info("=== JWT Configuration Validation Complete ===");
    }

    private boolean isUsingDefaultSecret() {
        return "default-secret-key-for-development-only-change-in-production".equals(jwtSecret);
    }
} 