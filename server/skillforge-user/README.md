# SkillForge User Service

## Overview

The SkillForge User Service is a Spring Boot microservice responsible for user management, authentication, and
user-related operations in the SkillForge learning platform. It provides comprehensive user functionality including
registration, authentication, profile management, course interactions, and skill tracking.

## Architecture

The user service is built with:

- **Spring Boot 3.x**: Core application framework
- **Spring Security**: Authentication and authorization
- **Spring Data MongoDB**: Data persistence
- **JWT (JSON Web Tokens)**: Stateless authentication
- **OpenAPI 3.0**: API documentation with Swagger UI
- **BCrypt**: Password hashing and security

## Core Functionality

### User Management

- User registration and authentication
- Profile management (CRUD operations)
- Password security with BCrypt hashing
- User search and discovery

### Course Interactions

- Course enrollment and unenrollment
- Course bookmarking and unbookmarking
- Course completion tracking
- Skills acquisition tracking

### Authentication & Security

- JWT token generation and validation
- Stateless authentication
- Role-based access control
- Inter-service communication security

## How the User Service Works

### Authentication Flow

1. **Registration**: User provides credentials → BCrypt password hashing → User stored in MongoDB
2. **Login**: User provides credentials → Validation against stored hash → JWT token generation
3. **Token Validation**: JWT token verified on each protected request
4. **Stateless Sessions**: No server-side session storage, all state in JWT tokens

### Request Processing

1. **Request Reception**: HTTP request received by controller
2. **Security Filter**: JWT token validation (if required)
3. **Business Logic**: Service layer processes request
4. **Data Persistence**: MongoDB operations via repository layer
5. **Response**: Structured JSON response with appropriate HTTP status

### Inter-Service Communication

The user service supports secure inter-service communication:

- **Service Key Authentication**: Internal endpoints protected with `X-Service-Key` header
- **Course Service Integration**: Allows course service to manage user enrollments, bookmarks, and completions
- **Stateless Design**: No shared state between services

## Authentication & Security

### JWT Implementation

The service uses JWT tokens for stateless authentication:

```java
// Token Generation
public String generateToken(String userId, String username) {
    return Jwts.builder()
            .subject(userId)
            .claim("username", username)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact();
}
```

### Security Configuration

#### Public Endpoints (No Authentication Required)

- `POST /api/v1/users/register` - User registration
- `POST /api/v1/users/login` - User authentication
- `GET /api/v1/users/health` - Health check
- `GET /docs/**` - API documentation
- `GET /actuator/*` - Monitoring endpoints

#### Protected Endpoints (JWT Required)

- All other `/api/v1/users/**` endpoints
- User profile operations
- Course interactions
- User search operations

#### Inter-Service Endpoints (Service Key Required)

- `POST /api/v1/users/{userId}/enroll/{courseId}` - Course enrollment
- `DELETE /api/v1/users/{userId}/enroll/{courseId}` - Course unenrollment
- `POST /api/v1/users/{userId}/bookmark/{courseId}` - Course bookmarking
- `DELETE /api/v1/users/{userId}/bookmark/{courseId}` - Course unbookmarking
- `POST /api/v1/users/{userId}/complete/{courseId}` - Course completion

### Password Security

- **BCrypt Hashing**: Passwords are hashed using BCrypt with configurable strength
- **Salt Generation**: Automatic salt generation for each password
- **Secure Comparison**: Timing-attack resistant password comparison

### CORS Configuration

Environment-specific CORS policies for cross-origin requests:

```java
// Development
config.addAllowedOriginPattern("*");
config.

setAllowedMethods(Arrays.asList("GET", "POST","PUT","DELETE","OPTIONS"));
        config.

setAllowedHeaders(List.of("*"));
```

## API Endpoints

### Authentication

- `POST /api/v1/users/register` - Register a new user
- `POST /api/v1/users/login` - Authenticate user and receive JWT token

### User Profile Management

- `GET /api/v1/users/{userId}/profile` - Get user profile
- `PUT /api/v1/users/{userId}/profile` - Update user profile
- `DELETE /api/v1/users/{userId}/profile` - Delete user profile

### Course Management

- `POST /api/v1/users/{userId}/enroll/{courseId}` - Enroll user in course
- `DELETE /api/v1/users/{userId}/enroll/{courseId}` - Unenroll user from course
- `POST /api/v1/users/{userId}/bookmark/{courseId}` - Bookmark a course
- `DELETE /api/v1/users/{userId}/bookmark/{courseId}` - Unbookmark a course
- `POST /api/v1/users/{userId}/complete/{courseId}` - Mark course as completed

### User Data Retrieval

- `GET /api/v1/users/{userId}/bookmarks` - Get bookmarked courses
- `GET /api/v1/users/{userId}/courses/enrolled` - Get enrolled courses
- `GET /api/v1/users/{userId}/courses/completed` - Get completed courses
- `GET /api/v1/users/{userId}/courses/bookmarked` - Get bookmarked courses
- `GET /api/v1/users/{userId}/skills` - Get user skills
- `GET /api/v1/users/{userId}/skills-in-progress` - Get skills in progress

### User Search & Discovery

- `GET /api/v1/users/with-skill` - Find users with specific skill
- `GET /api/v1/users/with-skill-in-progress` - Find users learning specific skill
- `GET /api/v1/users/enrolled-in/{courseId}` - Find users enrolled in course
- `GET /api/v1/users/completed/{courseId}` - Find users who completed course
- `GET /api/v1/users/bookmarked/{courseId}` - Find users who bookmarked course
- `GET /api/v1/users/search/user/{username}` - Search users by username
- `GET /api/v1/users/search/email/{email}` - Search users by email

### Health & Monitoring

- `GET /api/v1/users/health` - Service health check
- `GET /actuator/health` - Spring Boot health endpoint
- `GET /actuator/prometheus` - Prometheus metrics

### Documentation

- `GET /docs` - Swagger UI for API documentation
- `GET /user-openapi.yaml` - OpenAPI specification

## Data Models

### User Entity

```java
public class User {
    private String id;
    private String username;
    private String email;
    private String password; // BCrypt hashed
    private String firstName;
    private String lastName;
    private List<String> enrolledCourses;
    private List<String> completedCourses;
    private List<String> bookmarkedCourses;
    private List<String> skills;
    private List<String> skillsInProgress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### DTOs (Data Transfer Objects)

- **UserRegisterRequest**: Registration data validation
- **UserLoginRequest**: Authentication credentials
- **UserProfileUpdateRequest**: Profile update data
- **UserLoginResponse**: Authentication response with JWT
- **UserProfileResponse**: User profile data
- **UserRegisterResponse**: Registration confirmation

## Configuration

### Environment Variables

```bash
# Service Configuration
SERVER_PORT_USER=8082

# MongoDB Configuration
MONGODB_DATABASE=skillforge
MONGO_URL=mongodb://localhost:27017/skillforge

# JWT Configuration
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION_MS=86400000

# Service Keys (for inter-service communication)
COURSE_SERVICE_KEY=course-service-key
```

### Application Properties

```yaml
server:
  port: ${SERVER_PORT_USER:8082}
  address: "0.0.0.0"

spring:
  data:
    mongodb:
      database: ${MONGODB_DATABASE:skillforge}
      uri: ${MONGO_URL:mongodb://localhost:27017/skillforge}

jwt:
  secret: ${JWT_SECRET:default-secret-key}
  expirationMs: ${JWT_EXPIRATION_MS:86400000}
```

### Profiles

- **dev**: Development configuration with debug logging
- **docker**: Docker environment configuration
- **prod**: Production configuration with optimized settings
- **test**: Test configuration with in-memory database

## Development

### Running Locally

```bash
# 1. Start MongoDB
# On macOS with Homebrew:
brew services start mongodb-community

# On Ubuntu/Debian:
sudo systemctl start mongod

# On Windows:
# Download MongoDB from https://www.mongodb.com/try/download/community and run:
mongod

# Or using Docker:
docker run -d -p 27017:27017 --name mongodb mongo:latest

# 2. Set environment variables (optional - defaults are provided)
export SERVER_PORT_USER=8082
export MONGODB_DATABASE=skillforge
export MONGO_URL=mongodb://localhost:27017/skillforge
export JWT_SECRET=your-secret-key-here
export JWT_EXPIRATION_MS=86400000

# 3. Start the user service
./gradlew bootRun

# Or with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Testing

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests UserServiceTest

# Run with coverage
./gradlew test jacocoTestReport
```

### API Documentation

Once the service is running, access the API documentation:

#### Direct Access (when running user service standalone)

- **Swagger UI**: http://localhost:8082/docs
- **OpenAPI Spec**: http://localhost:8082/user-openapi.yaml (This will download the OpenAPI spec file)

#### Through Gateway (when running with gateway)

- **Swagger UI**: http://localhost:8081/api/v1/users/docs
- **OpenAPI Spec**: http://localhost:8081/api/v1/users/user-openapi.yaml (This will download the OpenAPI spec file)

### Database Operations

```bash
# Connect to MongoDB
mongosh mongodb://localhost:27017/skillforge

# View collections
show collections

# Query users
db.users.find()

# Query specific user
db.users.findOne({username: "testuser"})
```

## Monitoring and Logging

### Logging Configuration

```yaml
logging:
  level:
    com.gitittogether.skillforge.server.user: DEBUG
    org.springframework.security: DEBUG
    org.springframework.data.mongodb: DEBUG
```

### Health Checks

- **Service Health**: `GET /api/v1/users/health`
- **Spring Boot Health**: `GET /actuator/health`
- **Database Connectivity**: Included in health checks

### Metrics

- **Prometheus Metrics**: `GET /actuator/prometheus`
- **Application Metrics**: Request counts, response times, error rates
- **Database Metrics**: Connection pool, query performance

## Security Best Practices

### Implemented Security Measures

1. **Password Security**
    - BCrypt hashing with configurable strength
    - Automatic salt generation
    - Secure password comparison

2. **JWT Security**
    - HMAC-SHA256 signing
    - Configurable expiration times
    - Token validation on every request

3. **Input Validation**
    - Request DTO validation with Bean Validation
    - SQL injection prevention (MongoDB)
    - XSS protection through proper encoding

4. **CORS Configuration**
    - Environment-specific CORS policies
    - Proper preflight request handling
    - Secure header configuration

5. **Error Handling**
    - Structured error responses
    - No sensitive information leakage
    - Proper HTTP status codes

### Security Headers

The service includes security headers:

- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`

## Troubleshooting

### Common Issues

1. **MongoDB Connection Errors**
    - Ensure MongoDB is running and accessible
    - Check MongoDB host/port configuration
    - Verify database name and authentication

2. **JWT Validation Failures**
    - Check JWT secret configuration
    - Verify token format and expiration
    - Ensure proper Authorization header format

3. **Authentication Issues**
    - Verify user credentials in database
    - Check password hashing configuration
    - Ensure JWT secret is consistent across services

4. **CORS Errors**
    - Verify CORS configuration for environment
    - Check allowed origins and methods
    - Ensure proper preflight request handling

### Debug Mode

Enable debug logging for troubleshooting:

```yaml
logging:
  level:
    com.gitittogether.skillforge.server.user: DEBUG
    org.springframework.security: DEBUG
    org.springframework.data.mongodb: DEBUG
```

### Database Issues

```bash
# Check MongoDB status
sudo systemctl status mongod

# Check MongoDB logs
sudo journalctl -u mongod

# Test MongoDB connection
mongosh mongodb://localhost:27017/skillforge --eval "db.runCommand('ping')"
```

## Integration with Other Services

### Gateway Integration

- Receives requests through API Gateway
- JWT tokens validated by gateway
- User ID injected via `X-User-Id` header

### Course Service Integration

- Inter-service communication via service keys
- Course enrollment, bookmarking, and completion tracking
- User skill acquisition updates

### Monitoring Integration

- Prometheus metrics for monitoring
- Health checks for load balancers
- Structured logging for log aggregation 