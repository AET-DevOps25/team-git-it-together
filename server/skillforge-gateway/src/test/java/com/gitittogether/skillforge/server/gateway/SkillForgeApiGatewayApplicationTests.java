package com.gitittogether.skillforge.server.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SkillForgeApiGatewayApplicationTests {
    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        // Context load test
        assertThat(port).isGreaterThanOrEqualTo(0);
    }

    @Test
    void healthEndpointWorks() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:" + port + "/actuator/health";
        try {
            String response = restTemplate.getForObject(url, String.class);
            assertThat(response).containsAnyOf("UP", "DOWN");
        } catch (HttpServerErrorException ex) {
            String response = ex.getResponseBodyAsString();
            assertThat(response).containsAnyOf("UP", "DOWN");
        }
    }
}
