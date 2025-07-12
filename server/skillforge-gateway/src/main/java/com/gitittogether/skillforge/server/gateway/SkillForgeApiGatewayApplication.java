package com.gitittogether.skillforge.server.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration.class
})
public class SkillForgeApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillForgeApiGatewayApplication.class, args);
	}
}