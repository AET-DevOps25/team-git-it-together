# Rate Limiting Implementation Guide

## Overview

This document explains the rate limiting implementation in the SkillForge API Gateway using Spring Cloud Gateway's built-in `RequestRateLimiter` with Redis as the backing store.

## Architecture

```
Client Request → API Gateway → Rate Limiter (Redis) → Microservice
```

### Components

1. **RedisRateLimiter**: Spring Cloud Gateway's built-in rate limiter using Redis
2. **KeyResolver**: Determines the key for rate limiting (IP, User ID, Client ID)
3. **ReactiveRedisConnectionFactory**: Redis connection for reactive operations (auto-configured by Spring Boot)
4. **Gateway Routes**: Configured with rate limiting filters

## Configuration

### Java Configuration (Current Implementation)

The rate limiting is configured in Java using the following classes:

#### 1. RedisConfig.java
```java
@Configuration
public class RedisConfig {
    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        // Creates reactive Redis template with proper serialization
        // ReactiveRedisConnectionFactory is auto-configured by Spring Boot
    }
}
```

#### 2. RateLimitingConfig.java
```java
@Configuration
public class RateLimitingConfig {
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        // Creates Redis-based rate limiter with configurable limits
    }

    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        // Primary key resolver - resolves by client ID or IP address
        // Marked as @Primary to resolve bean conflicts
    }

    @Bean
    public KeyResolver authenticatedUserKeyResolver() {
        // Alternative key resolver for authenticated users
        // Can be used with @Qualifier when needed
    }
}
```

#### 3. GatewayConfig.java
```java
@Bean
public RouteLocator routes() {
    return builder.routes()
        .route("user-service-auth", r -> r.path("/api/v1/users/login")
            .filters(f -> f
                .requestRateLimiter(config -> config
                    .setRateLimiter(redisRateLimiter)
                    .setKeyResolver(userKeyResolver)  // Uses @Primary bean
                    .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
            .uri(userServiceUri))
        .build();
}
```

## Redis Configuration

### Spring Boot Auto-Configuration

Spring Boot automatically configures the `ReactiveRedisConnectionFactory` based on the properties in `application.yml`:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: ""
      timeout: 2000ms
      lettuce:
        pool:
          max-connections: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms
```

### Custom Redis Template

We only need to configure the `ReactiveRedisTemplate` for proper serialization:

```java
@Bean
public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
    StringRedisSerializer serializer = new StringRedisSerializer();
    
    RedisSerializationContext<String, String> serializationContext = 
        RedisSerializationContext.<String, String>newSerializationContext()
            .key(serializer)
            .value(serializer)
            .hashKey(serializer)
            .hashValue(serializer)
            .build();

    return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
}
```

### Environment Variables

```bash
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD= # Optional, but leave empty if no password (default is no password)
```

## Key Resolver Configuration

### Primary Key Resolver (`@Primary`)

The `userKeyResolver` is marked as `@Primary` to resolve bean conflicts with Spring Cloud Gateway's auto-configuration:

```java
@Bean
@Primary
public KeyResolver userKeyResolver() {
    return exchange -> {
        // Try to get client ID from header first
        String clientId = exchange.getRequest().getHeaders().getFirst("X-Client-ID");
        
        if (clientId != null && !clientId.isEmpty()) {
            return Mono.just(clientId);
        }
        
        // Fallback to IP address
        String ipAddress = exchange.getRequest().getRemoteAddress() != null ? 
            exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";
        
        return Mono.just("ip:" + ipAddress);
    };
}
```

### Alternative Key Resolver

The `authenticatedUserKeyResolver` can be used when you need user-specific rate limiting:

```java
@Bean
public KeyResolver authenticatedUserKeyResolver() {
    return exchange -> {
        // Try to get user ID from JWT token
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        
        if (userId != null && !userId.isEmpty()) {
            return Mono.just("user:" + userId);
        }
        
        // Fallback to IP address
        String ipAddress = exchange.getRequest().getRemoteAddress() != null ? 
            exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";
        
        return Mono.just("ip:" + ipAddress);
    };
}
```

## Rate Limiting Parameters

### Configuration Properties

```yaml
rate:
  limit:
    requests-per-minute: 60    # Default: 60 requests per minute
    requests-per-second: 10    # Default: 10 requests per second
    burst: 20                  # Default: 20 burst capacity
```

### Rate Limiter Configuration

- **replenishRate**: Tokens per second (calculated from requests per minute)
- **burstCapacity**: Maximum burst of requests allowed
- **keyResolver**: Strategy to determine rate limiting key

### Key Resolution Strategies

1. **Client ID**: Uses `X-Client-ID` header if present
2. **User ID**: Uses `X-User-Id` header for authenticated users
3. **IP Address**: Fallback to client IP address

## Route-Specific Configuration

### Public Endpoints (No Authentication)
- **Rate**: Higher limits
- **Key**: IP address or client ID
- **Examples**: `/api/v1/courses/public/**`

### Authentication Endpoints
- **Rate**: Moderate limits
- **Key**: IP address or client ID
- **Examples**: `/api/v1/users/login`, `/api/v1/users/register`

### Protected Endpoints
- **Rate**: Standard limits
- **Key**: User ID (if authenticated) or IP address
- **Examples**: `/api/v1/users/**`, `/api/v1/courses/**`

## Response Headers

Rate limiting adds the following headers to responses:

- `X-RateLimit-Limit`: Maximum requests allowed
- `X-RateLimit-Remaining`: Remaining requests in current window
- `X-RateLimit-Reset`: Time when the rate limit resets

## Error Handling

### Rate Limit Exceeded (429)
```json
{
  "error": "Too Many Requests",
  "message": "Rate limit exceeded",
  "status": 429,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Configuration Errors
- Redis connection failures are logged and handled gracefully
- Missing key resolvers fall back to IP-based limiting

## Monitoring and Debugging

### Logging Configuration
```yaml
logging:
  level:
    org.springframework.cloud.gateway.filter.ratelimit: DEBUG
    org.springframework.data.redis: DEBUG
```

## Testing

### Test Scripts
Use the provided test scripts to verify rate limiting:

```bash
# Comprehensive test script
./test-rate-limiting.sh

# Simple test script
./simple-rate-limit-test.sh
```

### Common Issues

1. **Redis Connection Errors**
   - Verify Redis is running: `redis-cli ping`
   - Check connection configuration in `application.yml`

2. **Type Mismatch Errors**
   - Ensure `ReactiveRedisConnectionFactory` is used (not `RedisConnectionFactory`)
   - Verify correct serialization context

3. **Rate Limiting Not Working**
   - Check if Redis is accessible
   - Verify key resolver is properly configured
   - Check logs for configuration errors

4. **Bean Conflict Errors**
   - Let Spring Boot auto-configure `ReactiveRedisConnectionFactory`
   - Only configure custom beans when necessary (like `ReactiveRedisTemplate`)
   - Use `@Primary` annotation to resolve multiple bean conflicts
   - Use `@Qualifier` to specify which bean to inject when needed

5. **Deprecation Warnings**
   - Use `spring.data.redis.*` instead of `spring.redis.*`
   - Use `max-connections` instead of `max-active`

### Debug Commands

#### If Redis is running locally (default):
```bash
# Check Redis connection
redis-cli -h 127.0.0.1 -p 6379 ping

# Monitor Redis operations
redis-cli -h 127.0.0.1 -p 6379 monitor

# Check rate limiting keys
redis-cli -h 127.0.0.1 -p 6379 keys "request_rate_limiter*"
```

#### If Redis is running in Docker Compose (recommended for this project):
```bash
# Check Redis connection
docker-compose exec redis redis-cli ping

# Monitor Redis operations
docker-compose exec redis redis-cli monitor

# Check rate limiting keys
docker-compose exec redis redis-cli keys "request_rate_limiter*"
```

#### If using Docker directly (container name: skillforge-redis or skillforge-redis-dev):
```bash
# Check Redis connection
docker exec -it skillforge-redis redis-cli ping

# Monitor Redis operations
docker exec -it skillforge-redis redis-cli monitor

# Check rate limiting keys
docker exec -it skillforge-redis redis-cli keys "request_rate_limiter*"
```