package com.gitittogether.skillforge.server.gateway.config;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            
            log.debug("JWT Filter: Token found, validating...");
            if (!jwtUtil.isTokenValid(token)) {
                log.warn("JWT Filter: Invalid token for request {} {}", method, path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
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
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
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
                         !path.contains("/api/v1/courses/public");
        
        log.debug("JWT Filter: Path {} is secured: {}", path, secured);
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
}
