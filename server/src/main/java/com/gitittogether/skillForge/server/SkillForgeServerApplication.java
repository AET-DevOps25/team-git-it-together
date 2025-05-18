package com.gitittogether.skillForge.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class SkillForgeServerApplication {

	public static void main(String[] args) {
		// Start application and get environment
		Environment env = SpringApplication.run(SkillForgeServerApplication.class, args).getEnvironment();

		// Log important environment info
		log.info("✅ Spring Boot application started successfully!");
		log.info("🧩 Active profile        : {}", env.getProperty("spring.profiles.active"));
		log.info("🌍 MongoDB URI          : {}", env.getProperty("spring.data.mongodb.uri"));
		log.info("🛢  MongoDB Database     : {}", env.getProperty("spring.data.mongodb.database"));
		log.info("🚀 Application name      : {}", env.getProperty("spring.application.name"));
		log.info("🌐 Server port           : {}", env.getProperty("server.port", "8080"));
	}
}
