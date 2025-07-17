package com.gitittogether.skillforge.server.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple controller exposing an endpoint that deliberately throws a runtime
 * exception so that we can generate 5xx errors and test Prometheus / Grafana
 * alerting and dashboards. Similar to the FastAPI endpoint in the GenAI
 * service.
 */
@RestController
@RequestMapping("/api/v1/debug")
public class DebugController {

    @GetMapping("/error")
    public void debugError() {
        throw new RuntimeException("Forced debug error for monitoring test");
    }
}
