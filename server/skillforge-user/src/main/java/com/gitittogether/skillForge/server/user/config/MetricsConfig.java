package com.gitittogether.skillForge.server.user.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Value("${spring.application.version:0.0.1-SNAPSHOT}")
    private String applicationVersion;

    /**
     * Registers a gauge that exposes the application version as a Prometheus metric.
     * This will be available as 'app_version_info' with a label 'version' containing the actual version string.
     */
    @Bean
    public Gauge versionGauge(MeterRegistry meterRegistry) {
        return Gauge
                .builder("app_version_info", () -> 1.0)
                .description("Application version information")
                .tag("version", applicationVersion)
                .tag("service", "user-service")
                .register(meterRegistry);
    }
    
    /**
     * User signup counter - tracks the number of new user registrations
     * This domain-specific metric helps understand user growth and onboarding
     */
    @Bean
    public Counter userSignupCounter(MeterRegistry meterRegistry) {
        return Counter
                .builder("user_signup_total")
                .description("Number of user signups")
                .tag("service", "user-service")
                .register(meterRegistry);
    }
    
    /**
     * Authentication failure counter - tracks failed login attempts
     * This is important for security monitoring and detecting potential brute force attacks
     */
    @Bean
    public Counter userAuthFailureCounter(MeterRegistry meterRegistry) {
        return Counter
                .builder("user_auth_failure_total")
                .description("Number of authentication failures")
                .tag("service", "user-service")
                .register(meterRegistry);
    }
    
}
