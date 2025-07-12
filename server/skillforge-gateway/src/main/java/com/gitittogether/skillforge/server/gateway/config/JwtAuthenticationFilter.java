package com.gitittogether.skillforge.server.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        log.info("JWT Filter: Processing request {} {} from {}", method, path, request.getRemoteAddress());

        if (isSecured(request)) {
            log.info("JWT Filter: Request {} {} is secured, checking authentication", method, path);

            final String token = getAuthHeader(request);
            if (token == null) {
                log.warn("JWT Filter: No Authorization header found for secured request {} {}", method, path);
                return writeJsonError(exchange, 401, "Unauthorized", "Missing or invalid Authorization header", path);
            }

            log.debug("JWT Filter: Token found, validating...");
            if (!jwtUtil.isTokenValid(token)) {
                log.warn("JWT Filter: Invalid token for request {} {}", method, path);
                return writeJsonError(exchange, 401, "Unauthorized", "Invalid JWT token", path);
            }

            try {
                Claims claims = jwtUtil.extractAllClaims(token);
                String userId = claims.getSubject();
                log.info("JWT Filter: Valid token for user {} on request {} {}", userId, method, path);

                exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .build();

                log.debug("JWT Filter: Added X-User-Id header: {}", userId);
            } catch (Exception e) {
                log.error("JWT Filter: Error extracting claims from token for request {} {}: {}",
                        method, path, e.getMessage(), e);
                return writeJsonError(exchange, 401, "Unauthorized", "Invalid JWT token: " + e.getMessage(), path);
            }
        } else {
            log.info("JWT Filter: Request {} {} is not secured, allowing through", method, path);
        }

        return chain.filter(exchange)
                .doOnSuccess(v -> log.debug("JWT Filter: Successfully processed request {} {}", method, path))
                .doOnError(throwable -> log.error("JWT Filter: Error processing request {} {}: {}",
                        method, path, throwable.getMessage(), throwable));
    }

    private boolean isSecured(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        boolean secured = !path.contains("/api/v1/users/login") &&
                !path.contains("/api/v1/users/register") &&
                !path.contains("/api/v1/courses/public") &&
                !path.contains("/api/v1/courses/search") &&
                !path.contains("/docs");

        log.info("JWT Filter: Path {} is secured: {}", path, secured);
        return secured;
    }

    private String getAuthHeader(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.debug("JWT Filter: Extracted token (first 10 chars): {}",
                    token.length() > 10 ? token.substring(0, 10) + "..." : token);
            return token;
        }
        log.debug("JWT Filter: No valid Authorization header found");
        return null;
    }

    private Mono<Void> writeJsonError(ServerWebExchange exchange, int status, String error, String message, String path) {
        exchange.getResponse().setStatusCode(HttpStatus.valueOf(status));
        exchange.getResponse().getHeaders().set("Content-Type", "application/json");

        // Add CORS headers to error responses
        exchange.getResponse().getHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponse().getHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponse().getHeaders().set("Access-Control-Allow-Headers", "*");

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", Instant.now().toString());
        errorBody.put("status", status);
        errorBody.put("error", error);
        errorBody.put("message", message);
        errorBody.put("path", path);
        ObjectMapper mapper = new ObjectMapper();
        try {
            byte[] bytes = mapper.writeValueAsBytes(errorBody);
            DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
            DataBuffer buffer = bufferFactory.wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            byte[] fallback = ("{\"error\":\"" + error + "\"}").getBytes(StandardCharsets.UTF_8);
            DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
            DataBuffer buffer = bufferFactory.wrap(fallback);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
    }
}
