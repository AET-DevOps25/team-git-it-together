package com.gitittogether.skillForge.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    /**
     * Endpoint to check the health of the server.
     *
     * @return a simple message indicating the server is up and running.
     */
    @GetMapping
    public ResponseEntity<String> getHealth() {
        return ResponseEntity.ok("Server is up and running!");
    }
}
