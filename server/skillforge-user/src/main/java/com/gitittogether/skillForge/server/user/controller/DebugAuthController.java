package com.gitittogether.skillForge.server.user.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the debug authentication failure metric for monitoring and alert testing.
 */
@RestController
@RequestMapping("/api/v1/debug")
public class DebugAuthController {

    private final Counter authFailureCounter;

    public DebugAuthController(MeterRegistry registry) {
        this.authFailureCounter = Counter.builder("user_auth_failure_total")
                .description("Number of failed user authentication attempts (debug)")
                .register(registry);
    }

}
