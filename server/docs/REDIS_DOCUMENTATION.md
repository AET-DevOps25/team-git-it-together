# Redis Documentation for SkillForge Platform

## 🎯 **Overview**

Redis (Remote Dictionary Server) is an in-memory data structure store used in the SkillForge platform for **rate
limiting** at the API Gateway level. This document explains how Redis works, why we use it, and how it's configured in
our microservices architecture.

---

## 🏗️ **What is Redis?**

### **Definition**

Redis is an **open-source, in-memory data structure store** that can be used as:

- **Database** - Persistent storage with snapshots
- **Cache** - Fast data retrieval
- **Message broker** - Pub/sub messaging
- **Rate limiter** - Request counting and throttling

### **Key Characteristics**

- **In-Memory**: Data stored in RAM for ultra-fast access
- **Persistent**: Can save data to disk for durability
- **Atomic Operations**: Thread-safe operations
- **Data Structures**: Strings, Lists, Sets, Hashes, Sorted Sets
- **Pub/Sub**: Real-time messaging capabilities

---

## 🔧 **Why Redis for Rate Limiting?**

### **Problems Without Rate Limiting**

```
❌ Unlimited API requests
❌ Potential DoS attacks
❌ Resource exhaustion
❌ Poor user experience
❌ High infrastructure costs
```

### **Benefits of Redis-Based Rate Limiting**

```
✅ Distributed rate limiting across multiple gateway instances
✅ Ultra-fast request counting (sub-millisecond)
✅ Automatic expiration of rate limit counters
✅ Atomic operations prevent race conditions
✅ Scalable across multiple servers
✅ Real-time rate limit enforcement
```

---

## 🏛️ **Redis Architecture in SkillForge**

### **System Architecture**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client        │    │   API Gateway   │    │   Redis         │
│                 │    │                 │    │                 │
│ • HTTP Requests │───▶│ • Rate Limiting │───▶│ • Rate Counters │
│ • JWT Tokens    │    │ • JWT Validation│    │ • Sliding Window│
│ • Headers       │    │ • Routing       │    │ • Distributed   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                    ┌─────────────────┐
                    │   Microservices │
                    │                 │
                    │ • User Service  │
                    │ • Course Service│
                    └─────────────────┘
```

### **Rate Limiting Flow**

1. **Request Arrives**: Client sends HTTP request to API Gateway
2. **Client Identification**: Gateway identifies client (IP or custom header)
3. **Redis Check**: Gateway checks Redis for current rate limit count
4. **Counter Update**: Increment counter in Redis with expiration
5. **Decision**: Allow or reject based on limits
6. **Response**: Return success or 429 (Too Many Requests)

---

## ⚙️ **Redis Configuration**

### **Connection Configuration**

```yaml
spring:
  redis:
    host: localhost          # Redis server hostname
    port: 6379              # Redis server port
    password: your-password  # Redis authentication (optional)
    timeout: 2000ms         # Connection timeout
    lettuce:
      pool:
        max-active: 8       # Maximum active connections
        max-idle: 8         # Maximum idle connections
        min-idle: 0         # Minimum idle connections
        max-wait: -1ms      # Maximum wait time for connection
```

### **Rate Limiting Configuration**

```yaml
rate:
  limit:
    requests-per-minute: 60    # Requests allowed per minute
    requests-per-second: 10    # Requests allowed per second
    burst: 20                  # Burst capacity for traffic spikes
```

### **Environment-Specific Settings**

#### **Development Environment**

```yaml
rate:
  limit:
    requests-per-minute: 120    # More relaxed for development
    requests-per-second: 20     # Allow bursts during development
    burst: 50                   # Higher burst limit
```

#### **Production Environment**

```yaml
rate:
  limit:
    requests-per-minute: 60     # Stricter limits
    requests-per-second: 10     # Prevent abuse
    burst: 20                   # Controlled bursts
```

---

## 🔍 **How Redis Rate Limiting Works**

### **1. Client Identification**

```java
private String getClientId(ServerHttpRequest request) {
    // Try to get client ID from header first
    String clientId = request.getHeaders().getFirst("X-Client-ID");

    if (clientId == null) {
        // Use IP address as fallback
        String ipAddress = request.getRemoteAddress() != null ?
                request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        clientId = "ip:" + ipAddress;
    }

    return clientId;
}
```

### **2. Rate Limit Key Generation**

```java
// Key format: rate_limit:{client_id}:{window_type}
String perSecondKey = "rate_limit:" + clientId + ":second";
String perMinuteKey = "rate_limit:" + clientId + ":minute";
String burstKey = "rate_limit:" + clientId + ":burst";
```

### **3. Redis Operations**

```java
private Mono<Boolean> checkLimit(String key, int limit, Duration window) {
    return redisTemplate.opsForValue().increment(key)
            .flatMap(count -> {
                if (count == 1) {
                    // First request, set expiration
                    return redisTemplate.expire(key, window)
                            .thenReturn(count <= limit);
                }
                return Mono.just(count <= limit);
            })
            .defaultIfEmpty(true); // If Redis is unavailable, allow request
}
```

### **4. Multi-Level Rate Limiting**

```java
// Check all rate limits
return Mono.zip(
        checkLimit(perSecondKey, requestsPerSecond, Duration.ofSeconds(1)),

checkLimit(perMinuteKey, requestsPerMinute, Duration.ofMinutes(1)),

checkLimit(burstKey, burstCapacity, Duration.ofSeconds(5))
        ).

map(results ->results.

getT1() &&results.

getT2() &&results.

getT3());
```

---

## 📊 **Redis Data Structures Used**

### **1. String (Key-Value)**

```redis
# Rate limit counters
rate_limit:192.168.1.100:second -> "15"
rate_limit:192.168.1.100:minute -> "45"
rate_limit:client123:burst -> "8"
```

### **2. Expiration (TTL)**

```redis
# Automatic expiration after time window
EXPIRE rate_limit:192.168.1.100:second 1
EXPIRE rate_limit:192.168.1.100:minute 60
EXPIRE rate_limit:client123:burst 5
```

### **3. Atomic Operations**

```redis
# Increment counter atomically
INCR rate_limit:192.168.1.100:second

# Check if key exists and set expiration
EXISTS rate_limit:192.168.1.100:second
EXPIRE rate_limit:192.168.1.100:second 1
```

---

## 🚀 **Redis Commands and Operations**

### **Basic Redis Commands**

```bash
# Connect to Redis
redis-cli

# Check if Redis is running
PING
# Response: PONG

# Set a key-value pair
SET rate_limit:test:second 1
EXPIRE rate_limit:test:second 1

# Get a value
GET rate_limit:test:second

# Increment a counter
INCR rate_limit:test:second

# Check if key exists
EXISTS rate_limit:test:second

# Get time to live
TTL rate_limit:test:second

# List all rate limit keys
KEYS rate_limit:*

# Monitor Redis operations in real-time
MONITOR
```

### **Rate Limiting Specific Commands**

```bash
# Check current rate limit for a client
GET rate_limit:192.168.1.100:second

# Increment rate limit counter
INCR rate_limit:192.168.1.100:second

# Set expiration for rate limit window
EXPIRE rate_limit:192.168.1.100:second 1

# Check multiple rate limits
MGET rate_limit:192.168.1.100:second rate_limit:192.168.1.100:minute

# Delete all rate limit keys (for testing)
DEL rate_limit:*
```

---

## 🔧 **Redis Configuration in SkillForge**

### **1. RedisConfig.java**

```java

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

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
}
```

### **2. RateLimitingConfig.java**

```java

@Configuration
public class RateLimitingConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter(ReactiveRedisConnectionFactory connectionFactory) {
        return new RedisRateLimiter(
                requestsPerSecond,  // replenishRate (tokens per second)
                burstCapacity       // burstCapacity
        );
    }

    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String clientId = exchange.getRequest().getHeaders().getFirst("X-Client-ID");

            if (clientId != null && !clientId.isEmpty()) {
                return Mono.just(clientId);
            }

            String ipAddress = exchange.getRequest().getRemoteAddress() != null ?
                    exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";

            return Mono.just("ip:" + ipAddress);
        };
    }
}
```

---

## 📈 **Redis Performance and Monitoring**

### **Performance Metrics**

```bash
# Check Redis memory usage
INFO memory

# Check Redis performance
INFO stats

# Check Redis connections
INFO clients

# Check Redis keyspace
INFO keyspace
```

### **Rate Limiting Monitoring**

```bash
# Monitor rate limiting keys
redis-cli keys "rate_limit:*"

# Count rate limiting keys
redis-cli keys "rate_limit:*" | wc -l

# Monitor rate limiting in real-time
redis-cli monitor | grep rate_limit

# Check specific client rate limits
redis-cli mget rate_limit:192.168.1.100:second rate_limit:192.168.1.100:minute
```

### **Health Checks**

```bash
# Check Redis connectivity
redis-cli ping

# Check Redis info
redis-cli info

# Check Redis server time
redis-cli time
```

---

## 🛠️ **Redis Troubleshooting**

### **Common Issues**

#### **1. Redis Connection Issues**

```bash
# Check if Redis is running
docker ps | grep redis

# Check Redis logs
docker logs skillforge-redis

# Test Redis connectivity
docker exec skillforge-redis redis-cli ping

# Check Redis configuration
docker exec skillforge-redis redis-cli config get "*"
```

#### **2. Rate Limiting Not Working**

```bash
# Check rate limiting configuration
curl http://localhost:8081/actuator/env | grep rate.limit

# Verify Redis keys exist
redis-cli keys "rate_limit:*"

# Check gateway logs
docker logs skillforge-gateway | grep "Rate limiting"

# Test rate limiting manually
for i in {1..30}; do
  curl -X GET http://localhost:8081/api/v1/courses/public
  echo "Request $i"
done
```

#### **3. Redis Memory Issues**

```bash
# Check Redis memory usage
redis-cli info memory

# Check Redis memory fragmentation
redis-cli memory stats

# Clear all keys (for testing only)
redis-cli flushall

# Clear only rate limiting keys
redis-cli keys "rate_limit:*" | xargs redis-cli del
```

### **Debug Commands**

```bash
# Enable Redis slow log
redis-cli config set slowlog-log-slower-than 1000

# View Redis slow log
redis-cli slowlog get 10

# Monitor Redis commands in real-time
redis-cli monitor

# Check Redis latency
redis-cli --latency

# Check Redis latency history
redis-cli --latency-history
```

---

## 🔒 **Redis Security**

### **Authentication**

```yaml
spring:
  redis:
    password: your-secure-redis-password
```

### **Network Security**

```yaml
# Docker network isolation
networks:
  skillforge-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

### **Redis Security Best Practices**

1. **Use Strong Passwords**: Always set Redis password
2. **Network Isolation**: Run Redis in private network
3. **Disable Dangerous Commands**: Disable FLUSHALL, CONFIG, etc.
4. **Regular Updates**: Keep Redis updated
5. **Monitoring**: Monitor Redis access and performance

---

## 📚 **Redis Learning Resources**

### **Official Documentation**

- [Redis Official Documentation](https://redis.io/documentation)
- [Redis Commands Reference](https://redis.io/commands)
- [Redis Data Types](https://redis.io/topics/data-types)

### **Spring Boot Redis**

- [Spring Boot Redis Documentation](https://spring.io/projects/spring-data-redis)
- [Spring Cloud Gateway Rate Limiting](https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-redis-rate-limiter)

### **Rate Limiting Patterns**

- [Token Bucket Algorithm](https://en.wikipedia.org/wiki/Token_bucket)
- [Sliding Window Rate Limiting](https://en.wikipedia.org/wiki/Rate_limiting)
- [Distributed Rate Limiting](https://redis.io/topics/patterns-distributed-locks)

---

## 🎯 **Summary**

Redis in the SkillForge platform serves as a **high-performance, distributed rate limiting solution** that:

- ✅ **Prevents API abuse** through multi-level rate limiting
- ✅ **Ensures fair usage** across all clients
- ✅ **Provides real-time protection** against DoS attacks
- ✅ **Scales horizontally** across multiple gateway instances
- ✅ **Offers sub-millisecond performance** for rate limit checks
- ✅ **Automatically manages** rate limit windows and expiration

The implementation uses Redis's **atomic operations** and **automatic expiration** to provide reliable, scalable rate
limiting that protects our microservices while maintaining excellent performance. 