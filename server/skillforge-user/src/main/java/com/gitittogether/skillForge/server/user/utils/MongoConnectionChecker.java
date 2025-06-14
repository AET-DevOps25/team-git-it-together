package com.gitittogether.skillForge.server.user.utils;

import com.mongodb.client.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile(value = {"dev", "prod", "docker"})
@Slf4j
@Component
public class MongoConnectionChecker implements ApplicationRunner {

    private final MongoClient mongoClient;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    public MongoConnectionChecker(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("🔌 Checking MongoDB connection to database '{}'", databaseName);
        try {
            Document pingResult = mongoClient
                    .getDatabase(databaseName)
                    .runCommand(new Document("ping", 1));

            log.info("✅ Successfully connected to MongoDB '{}': {}", databaseName, pingResult.toJson());
        } catch (Exception e) {
            log.error("❌ Failed to connect to MongoDB '{}': {}", databaseName, e.getMessage());
        }
    }
}
