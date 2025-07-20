# SkillForge API Gateway

## Overview

The SkillForge API Gateway is a Spring Cloud Gateway-based service that acts as the single entry point for all client
requests to the SkillForge microservices architecture. It provides centralized routing, authentication, rate limiting,
and security controls for the entire application.

## Architecture

The gateway serves as a reverse proxy that:

- Routes requests to appropriate microservices (User Service, Course Service)
- Validates JWT tokens for protected endpoints
- Implements rate limiting to prevent abuse
- Provides CORS configuration for cross-origin requests
- Offers centralized logging and monitoring

## How the Gateway Works

### Request Flow

1. **Client Request**: Client sends HTTP request to gateway (port 8081)
2. **Route Matching**: Gateway matches request path to configured routes
3. **Filter Chain**: Request passes through configured filters in order:
    - CORS Filter (if applicable)
    - Rate Limiting Filter (if applicable)
    - JWT Authentication Filter (if applicable)
    - Logging Filter
4. **Service Routing**: Request is forwarded to appropriate microservice
5. **Response**: Response flows back through filters to client

### Route Categories

The gateway organizes routes into distinct categories with different security and rate limiting policies:

#### 1. Health Check Routes

- **Paths**: `/actuator/health`, `/api/v1/users/health`, `/api/v1/courses/health`
- **Authentication**: None required
- **Rate Limiting**: None
- **Purpose**: Service health monitoring

#### 2. Documentation Routes

- **Paths**: `/api/v1/users/docs/**`, `/api/v1/courses/docs/**`
- **Authentication**: None required
- **Rate Limiting**: None
- **Purpose**: API documentation access (Swagger UI)

#### 3. Public Authentication Routes

- **Paths**: `/api/v1/users/login`, `/api/v1/users/register`
- **Authentication**: None required
- **Rate Limiting**: Applied (prevents brute force attacks)
- **Purpose**: User authentication endpoints

#### 4. Public Course Routes

- **Paths**: `/api/v1/courses/public/**`
- **Authentication**: None required
- **Rate Limiting**: Applied
- **Purpose**: Public course content access

#### 5. Protected Routes

- **Paths**: `/api/v1/users/**`, `/api/v1/courses/**` (excluding public paths)
- **Authentication**: JWT token required
- **Rate Limiting**: Applied
- **Purpose**: Protected user and course operations

## Rate Limiting

### Configuration

The gateway implements Redis-based rate limiting with configurable parameters:

```yaml
rate:
  limit:
    requests-per-minute: 50    # Default: 50 requests per minute
    requests-per-second: 20    # Default: 20 requests per second
    burst: 50                  # Default: 50 burst capacity
```

### Implementation

- **Redis Rate Limiter**: Uses Spring Cloud Gateway's built-in `RedisRateLimiter`
- **Redis Connection**: Spring Cloud Gateway manages Redis connections internally using the configured
  `ReactiveRedisConnectionFactory`
- **Token Bucket Algorithm**: Implements token bucket with configurable replenish rate and burst capacity
- **Key Resolution**: Rate limiting keys are resolved by:
    1. `X-Client-ID` header (if provided)
    2. Client IP address (fallback)

### Rate Limiting Behavior

- **429 Too Many Requests**: Returns HTTP 429 when rate limit exceeded
- **Per-Client Isolation**: Each client (IP or Client ID) has independent rate limits
- **Graceful Degradation**: Rate limiting prevents service overload while maintaining availability

## Authentication Validation

### JWT Token Processing

The gateway validates JWT tokens for protected routes using a custom `JwtAuthenticationFilter`:

#### Token Validation Process

1. **Route Security Check**: Determines if route requires authentication
2. **Token Extraction**: Extracts Bearer token from `Authorization` header
3. **Token Validation**: Validates token signature, expiration, and format
4. **Claims Extraction**: Extracts user information from token claims
5. **Header Injection**: Adds `X-User-Id` header for downstream services

#### Secured vs Unsecured Routes

**Unsecured Routes** (no JWT required):

- `/api/v1/users/login`
- `/api/v1/users/register`
- `/api/v1/courses/public/**`
- `/api/v1/courses/search`
- All documentation routes (`/docs`)

**Secured Routes** (JWT required):

- All other `/api/v1/users/**` routes
- All other `/api/v1/courses/**` routes

#### Error Handling

- **401 Unauthorized**: Missing or invalid Authorization header
- **401 Unauthorized**: Invalid JWT token (expired, malformed, etc.)
- **JSON Error Response**: Structured error responses with timestamp, status, and message

## Routing

### Route Configuration

Routes are configured in `GatewayConfig.java` using Spring Cloud Gateway's `RouteLocatorBuilder`:

```java
.route("user-service-protected",r ->r.

path("/api/v1/users/**")
    .

and()
    .

not(p ->p.

path("/api/v1/users/docs/**","/api/v1/users/login","/api/v1/users/register"))
        .

filters(f ->f
        .

filter(jwtFilter)
        .

requestRateLimiter(config ->config
        .

setRateLimiter(redisRateLimiter)
            .

setKeyResolver(userKeyResolver)))
        .

uri(userServiceUri))
```

### Path Rewriting

The gateway uses path rewriting for documentation routes:

- `/api/v1/users/docs` → `/swagger-ui/index.html`
- `/api/v1/courses/docs` → `/swagger-ui/index.html`
- `/api/v1/courses/course-openapi.yaml` → `/course-openapi.yaml`

### Service URIs

- **User Service**: `http://localhost:8082` (configurable via `user.service.uri`)
- **Course Service**: `http://localhost:8083` (configurable via `course.service.uri`)

## Security

### Security Configuration

The gateway implements multiple security layers:

#### 1. Spring Security

- **CSRF Protection**: Disabled (not needed for API gateway)
- **CORS**: Configured per environment
- **HTTP Basic Auth**: Disabled (using JWT instead)

#### 2. CORS Configuration

Environment-specific CORS policies:

```java
// Development
config.addAllowedOriginPattern("*");
config.

setAllowedMethods(Arrays.asList("GET", "POST","PUT","PATCH","DELETE","OPTIONS"));
        config.

setAllowedHeaders(List.of("*"));
```

#### 3. JWT Security

- **Token Validation**: Signature verification, expiration checks
- **User Context**: Injects user ID into request headers
- **Error Handling**: Secure error responses without information leakage

#### 4. Rate Limiting Security

- **DDoS Protection**: Prevents abuse through rate limiting
- **Client Isolation**: Separate limits per client/IP
- **Configurable Limits**: Environment-specific rate limiting

### Security Best Practices

1. **No Sensitive Data Logging**: JWT tokens are truncated in logs
2. **Structured Error Responses**: Consistent error format without information leakage
3. **CORS Headers**: Proper CORS configuration for cross-origin requests
4. **Request Validation**: Validates all incoming requests before processing

## Configuration

### Environment Variables

```bash
# Service Ports
SERVER_PORT_GATEWAY=8081
SERVER_PORT_USER=8082
SERVER_PORT_COURSES=8083

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# Rate Limiting
RATE_LIMIT_REQUESTS_PER_MINUTE=50
RATE_LIMIT_REQUESTS_PER_SECOND=20
RATE_LIMIT_BURST=50
```

### Profiles

- **dev**: Development configuration with debug logging
- **docker**: Docker environment configuration
- **prod**: Production configuration with optimized settings

## Monitoring and Logging

### Logging Levels

```yaml
logging:
  level:
    com.gitittogether.skillforge.server.gateway: DEBUG
    org.springframework.cloud.gateway: DEBUG
    org.springframework.security: DEBUG
    org.springframework.data.redis: DEBUG
```

### Metrics

- **Request Count**: Number of requests processed
- **Response Times**: Gateway processing latency
- **Error Rates**: Authentication and rate limiting failures
- **Redis Metrics**: Rate limiting performance

## Development

### Running Locally

```bash
# 1. Start Redis (required for rate limiting)
# On macOS with Homebrew:
brew services start redis

# On Ubuntu/Debian:
sudo systemctl start redis-server

# On Windows:
# Download Redis from https://redis.io/download and run:
redis-server

# Or using Docker:
docker run -d -p 6379:6379 redis:latest

# 2. Set environment variables (optional - defaults are provided)
export SERVER_PORT_GATEWAY=8081
export SERVER_PORT_USER=8082
export SERVER_PORT_COURSES=8083
export REDIS_HOST=localhost
export REDIS_PORT=6379
export RATE_LIMIT_REQUESTS_PER_MINUTE=50
export RATE_LIMIT_REQUESTS_PER_SECOND=20
export RATE_LIMIT_BURST=50
export JWT_SECRET=your-secret-key
export JWT_EXPIRATION_MS=86400000

# 3. Start the gateway
./gradlew bootRun

# Or with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Testing

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests GatewayConfigTest
```

### Gateway Testing Script

The gateway includes a comprehensive testing script that validates security, performance, and functionality:

```bash
# Navigate to the testing scripts directory
cd ../testing-scripts

# Run the gateway test script
python test_gateway.py
```

#### What the Test Script Does

The `test_gateway.py` script performs comprehensive testing of the API Gateway:

1. **Service Health Checks**
    - Verifies all services (Gateway, User Service, Course Service) are running
    - Tests health endpoints

2. **Authentication Testing**
    - Creates a test user account
    - Tests user registration and login
    - Validates JWT token generation and usage
    - Tests both username and email login

3. **Security Testing**
    - **Public Endpoints**: Verifies public endpoints are accessible
    - **Protected Endpoints**: Ensures protected endpoints reject unauthenticated requests
    - **Authenticated Access**: Tests protected endpoints with valid JWT tokens
    - **Direct Access Blocking**: Confirms direct microservice access is blocked
    - **Security Headers**: Validates presence of security headers

4. **Rate Limiting Testing**
    - **Burst Testing**: Sends rapid requests to test burst rate limiting
    - **Sustained Testing**: Tests sustained rate limiting over time
    - **Endpoint Testing**: Tests rate limiting across different endpoints
    - **Analysis**: Provides detailed rate limiting statistics

5. **Performance Testing**
    - **Latency Measurement**: Measures response times for multiple requests
    - **Statistical Analysis**: Calculates average, median, min, and max latency
    - **Performance Validation**: Ensures latency is within acceptable limits

6. **Report Generation**
    - Creates detailed test reports in JSON format
    - Generates comprehensive markdown report (`TESTING_REPORT.md`)
    - Provides executive summary with pass/fail status
    - Includes performance metrics and recommendations

#### Test Results

The script creates a timestamped results directory containing:

- `test.log` - Detailed test execution log
- `user_info.txt` - Test user credentials and ID
- `latency_results.json` - Performance metrics
- `rate_limit_results.json` - Rate limiting analysis
- `TESTING_REPORT.md` - Comprehensive test report

#### Prerequisites

Before running the test script, ensure:

1. **All services are running**:
    - Gateway (port 8081)
    - User Service (port 8082)
    - Course Service (port 8083)
    - Redis (for rate limiting)
    - MongoDB (So that user adn course services can function properly)

2. **Python is available** (uses only built-in libraries)

3. **Network connectivity** between services

#### Example Output

```bash
$ python test_gateway.py

[2025-07-20 13:30:00] Starting API Gateway Security and Performance Tests
[2025-07-20 13:30:00] Results will be saved to: test-results-20250720-133000
[2025-07-20 13:30:01] ✓ Gateway is running
[2025-07-20 13:30:01] ✓ User Service is running
[2025-07-20 13:30:01] ✓ Course Service is running
[2025-07-20 13:30:02] ✓ User registration successful (status: 201)
[2025-07-20 13:30:02] ✓ JWT token and user ID obtained successfully
[2025-07-20 13:30:02] ✓ Email login successful - JWT token and user ID obtained
[2025-07-20 13:30:03] ✓ Public endpoint accessible: http://localhost:8081/api/v1/courses/public
[2025-07-20 13:30:04] ✓ Protected endpoint correctly rejects unauthenticated requests
[2025-07-20 13:30:05] ✓ Protected endpoint accessible with JWT
[2025-07-20 13:30:06] ✓ Rate limiting is working (hit 15 times)
[2025-07-20 13:30:07] ✓ Average latency: 45.23ms
[2025-07-20 13:30:08] ✓ X-Content-Type-Options header present

============================================================
🚦 API GATEWAY TEST SUMMARY
============================================================
📊 Total Tests: 7
✅ Passed: 7
❌ Failed: 0
📈 Success Rate: 100.0%
🎯 Status: 🟢 PASSED
📁 Results: test-results-20250720-133000
📄 Report: TESTING_REPORT.md
⏱️  Avg Latency: 45.23ms
🚦 Rate Limited: 15/180 requests
============================================================

🎉 All tests passed!
```

#### Troubleshooting

If tests fail:

1. **Service Connectivity Issues**:
   ```bash
   # Check if services are running
   curl http://localhost:8081/actuator/health
   curl http://localhost:8082/api/v1/users/health
   curl http://localhost:8083/api/v1/courses/health
   ```

2. **Redis Issues**:
   ```bash
   # Check Redis connectivity
   redis-cli ping
   ```

3. **Rate Limiting Not Working**:
    - Verify Redis is running and accessible
    - Check gateway rate limiting configuration
    - Review Redis connection settings

4. **Authentication Issues**:
    - Check JWT secret configuration
    - Verify user service is responding correctly
    - Review gateway JWT validation settings

### Adding New Routes

1. **Update GatewayConfig.java**: Add new route configuration
2. **Configure Security**: Determine if route needs authentication
3. **Set Rate Limiting**: Configure appropriate rate limits
4. **Add Tests**: Create tests for new route behavior
5. **Update Documentation**: Document new route in this README

## Troubleshooting

### Common Issues

1. **Redis Connection Errors**
    - Ensure Redis is running and accessible
    - Check Redis host/port configuration
    - Verify Redis password if configured

2. **JWT Validation Failures**
    - Check JWT secret configuration
    - Verify token format and expiration
    - Ensure proper Authorization header format

3. **Rate Limiting Issues**
    - Check Redis connectivity
    - Verify rate limiting configuration
    - Monitor Redis memory usage

4. **CORS Errors**
    - Verify CORS configuration for environment
    - Check allowed origins and methods
    - Ensure proper preflight request handling

### Debug Mode

Enable debug logging for troubleshooting:

```yaml
logging:
  level:
    com.gitittogether.skillforge.server.gateway: DEBUG
    org.springframework.cloud.gateway: DEBUG
```

## API Endpoints

### Health Checks

- `GET /actuator/health` - Gateway health
- `GET /api/v1/users/health` - User service health (Ensure the user service is running)
- `GET /api/v1/courses/health` - Course service health (Ensure the course service is running)

### Documentation

- `GET /api/v1/users/docs` - User service API docs (Ensure the user service is running)
- `GET /api/v1/courses/docs` - Course service API docs (Ensure the course service is running)

### Authentication

- `POST /api/v1/users/login` - User login (Ensure the user service is running)
- `POST /api/v1/users/register` - User registration (Ensure the user service is running)

### Public Content

- `GET /api/v1/courses/public/**` - Public course content (Ensure the course service is running)

### Protected Resources

- `GET /api/v1/users/**` - User operations (requires JWT) (Ensure the user service is running)
- `GET /api/v1/courses/**` - Course operations (requires JWT) (Ensure the course service is running)