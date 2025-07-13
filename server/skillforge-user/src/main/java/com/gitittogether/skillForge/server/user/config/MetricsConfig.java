package com.gitittogether.skillForge.server.user.config;

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
}
