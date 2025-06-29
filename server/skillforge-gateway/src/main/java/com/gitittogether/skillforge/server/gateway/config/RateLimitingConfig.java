package com.gitittogether.skillforge.server.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class RateLimitingConfig {

    @Value("${rate.limit.requests-per-minute:60}")
    private int requestsPerMinute;

    @Value("${rate.limit.requests-per-second:10}")
    private int requestsPerSecond;

    @Value("${rate.limit.burst:20}")
    private int burstCapacity;

    /**
     * Redis-based rate limiter using Spring Cloud Gateway's built-in RedisRateLimiter
     */
    @Bean
    public RedisRateLimiter redisRateLimiter(ReactiveRedisConnectionFactory connectionFactory) {
        log.info("Creating Redis rate limiter with {} requests per minute, {} per second, burst: {}", 
                requestsPerMinute, requestsPerSecond, burstCapacity);
        
        // Use the requests per second directly as replenish rate
        // Ensure we use integer values to avoid Lua script errors
        int replenishRate = Math.max(1, requestsPerSecond); // Minimum 1 token per second
        int burst = Math.max(1, burstCapacity); // Minimum burst of 1
        
        log.info("Redis rate limiter configured with replenishRate: {}, burstCapacity: {}", replenishRate, burst);
        
        return new RedisRateLimiter(
            replenishRate,  // replenishRate (tokens per second) - must be integer
            burst          // burstCapacity - must be integer
        );
    }

    /**
     * Primary key resolver for rate limiting - uses client IP or custom header
     * Marked as @Primary to resolve bean conflict with Spring Cloud Gateway auto-configuration
     */
    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Try to get client ID from header first
            String clientId = exchange.getRequest().getHeaders().getFirst("X-Client-ID");
            
            if (clientId != null && !clientId.isEmpty()) {
                log.debug("Rate limiting by client ID: {}", clientId);
                return Mono.just(clientId);
            }
            
            // Fallback to IP address
            String ipAddress = exchange.getRequest().getRemoteAddress() != null ? 
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";
            
            log.debug("Rate limiting by IP address: {}", ipAddress);
            return Mono.just("ip:" + ipAddress);
        };
    }

    /**
     * Alternative key resolver for authenticated users
     * Can be used with @Qualifier("authenticatedUserKeyResolver") when needed
     */
    @Bean
    public KeyResolver authenticatedUserKeyResolver() {
        return exchange -> {
            // Try to get user ID from JWT token
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            
            if (userId != null && !userId.isEmpty()) {
                log.debug("Rate limiting by user ID: {}", userId);
                return Mono.just("user:" + userId);
            }
            
            // Fallback to IP address
            String ipAddress = exchange.getRequest().getRemoteAddress() != null ? 
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";
            
            log.debug("Rate limiting by IP address (fallback): {}", ipAddress);
            return Mono.just("ip:" + ipAddress);
        };
    }
} 