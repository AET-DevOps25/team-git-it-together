package com.gitittogether.skillForge.server.user.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/docs")
public class DocsController {

    @GetMapping
    public ResponseEntity<String> getSwaggerUI() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/swagger-ui/index.html");
        String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        
        // Replace the hardcoded OpenAPI spec URL with the correct relative path for direct access
        html = html.replace("url: '/api/v1/users/user-openapi.yaml'", "url: './user-openapi.yaml'");
        
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }
} 