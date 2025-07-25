package com.gitittogether.skillForge.server.course.controller.utils;

import com.mongodb.client.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/courses/health")
@Slf4j
public class HealthController {

    private final MongoClient mongoClient;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    public HealthController(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    /**
     * Health check endpoint that verifies MongoDB connectivity.
     *
     * @return 200 OK if MongoDB responds; 503 if it fails.
     */
    @GetMapping
    public ResponseEntity<String> getHealth() {
        log.info("⚙️  Course service health check endpoint called");
        try {
            // Parse database name from URI
            String databaseName;
            try {
                URI uri = new URI(mongoUri);
                String path = uri.getPath();
                databaseName = path.substring(path.lastIndexOf("/") + 1);
            } catch (Exception e) {
                log.error("❌ Invalid MongoDB URI '{}': {}", mongoUri, e.getMessage());
                throw new IllegalArgumentException("Invalid MongoDB URI", e);
            }
            
            log.info("Pinging MongoDB database: {}", databaseName);
            Document pingResult = mongoClient
                    .getDatabase(databaseName)
                    .runCommand(new Document("ping", 1));
            log.info("✅ MongoDB ping succeeded: {}", pingResult.toJson());
            return ResponseEntity.ok("Course service is up — MongoDB ping OK: " + pingResult.toJson());
        } catch (Exception e) {
            log.error("❌ MongoDB ping failed", e);
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Course service is up — but MongoDB ping failed: " + e.getMessage());
        }
    }
}
