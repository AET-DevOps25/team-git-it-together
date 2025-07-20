# SkillForge Course Service

## Overview

The SkillForge Course Service is a Spring Boot microservice responsible for course management, content delivery, and
learning path generation in the SkillForge learning platform. It provides comprehensive course functionality including
creation, management, search, enrollment tracking, and AI-powered course generation.

## Architecture

The course service is built with:

- **Spring Boot 3.x**: Core application framework
- **Spring Security**: Authentication and authorization
- **Spring Data MongoDB**: Data persistence
- **JWT (JSON Web Tokens)**: Stateless authentication
- **OpenAPI 3.0**: API documentation with Swagger UI
- **GenAI Integration**: AI-powered course generation and content creation
- **Web Crawling**: Content extraction from external sources

## Core Functionality

### Course Management

- Course creation, updating, and deletion
- Course publishing and visibility controls
- Module and lesson management
- Course metadata and categorization

### Course Discovery & Search

- Advanced search with multiple filters
- Public course browsing
- Category and skill-based filtering
- Instructor and level-based search

### User-Course Interactions

- Course enrollment and unenrollment
- Course bookmarking and unbookmarking
- Course completion tracking
- Progress monitoring and skill acquisition

### AI-Powered Features

- GenAI course generation from learning paths
- AI-assisted content creation
- Web crawling for content extraction
- Intelligent course recommendations

### Inter-Service Communication

- User service integration for enrollment management
- Secure service-to-service communication
- User progress synchronization

## How the Course Service Works

### Request Processing Flow

1. **Request Reception**: HTTP request received by controller
2. **Security Filter**: JWT token validation (if required)
3. **Business Logic**: Service layer processes request
4. **Data Persistence**: MongoDB operations via repository layer
5. **Inter-Service Calls**: User service communication (if needed)
6. **Response**: Structured JSON response with appropriate HTTP status

### Course Lifecycle

1. **Course Creation**: Manual creation or AI generation
2. **Content Development**: Module and lesson addition
3. **Publishing**: Course made available to users
4. **Enrollment**: Users enroll in courses
5. **Progress Tracking**: User progress and completion monitoring
6. **Analytics**: Course performance and user engagement metrics

### AI Integration

The service integrates with GenAI for:

- **Course Generation**: Creating courses from learning path requests
- **Content Enhancement**: AI-assisted content creation
- **Web Crawling**: Extracting content from external URLs
- **Intelligent Recommendations**: Suggesting courses based on user preferences

## Authentication & Security

### JWT Implementation

The service validates JWT tokens for protected endpoints:

```java
// Token Validation
public boolean isTokenValid(String token, String userId) {
    try {
        final String subject = extractUserId(token);
        final Date expiration = extractClaim(token, Claims::getExpiration);
        return (subject.equals(userId) && !expiration.before(new Date()));
    } catch (JwtException | IllegalArgumentException e) {
        return false;
    }
}
```

### Security Configuration

#### Public Endpoints (No Authentication Required)

- `GET /api/v1/courses/public/**` - Public course browsing
- `GET /api/v1/courses/search` - Course search functionality
- `GET /api/v1/courses/categories/**` - Category information
- `GET /api/v1/courses/health` - Health check
- `GET /docs/**` - API documentation
- `GET /actuator/*` - Monitoring endpoints

#### Protected Endpoints (JWT Required)

- All other `/api/v1/courses/**` endpoints
- Course creation and management
- User enrollment operations
- Course generation and AI features

### Inter-Service Security

The course service communicates with the user service using:

- **Service Key Authentication**: `X-Service-Key` header for internal operations
- **Secure Headers**: User ID injection via `X-User-Id` header
- **Stateless Design**: No shared state between services

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

### Course Management

- `POST /api/v1/courses` - Create a new course
- `GET /api/v1/courses/{courseId}` - Get course details
- `GET /api/v1/courses` - Get all courses
- `PUT /api/v1/courses/{courseId}` - Update course
- `PATCH /api/v1/courses/{courseId}` - Partial course update
- `DELETE /api/v1/courses/{courseId}` - Delete course

### Public Course Access

- `GET /api/v1/courses/public` - Get public courses
- `GET /api/v1/courses/published` - Get published courses

### Course Search & Discovery

- `GET /api/v1/courses/search` - Advanced search with filters
- `GET /api/v1/courses/search/instructor/{instructor}` - Search by instructor
- `GET /api/v1/courses/search/level/{level}` - Search by level
- `GET /api/v1/courses/search/language/{language}` - Search by language
- `GET /api/v1/courses/search/skill/{skillName}` - Search by skill
- `GET /api/v1/courses/search/category/{categoryName}` - Search by category
- `GET /api/v1/courses/search/title/{title}` - Fuzzy title search

### User-Course Interactions

- `POST /api/v1/courses/{courseId}/enroll/{userId}` - Enroll user in course
- `DELETE /api/v1/courses/{courseId}/enroll/{userId}` - Unenroll user from course
- `POST /api/v1/courses/{courseId}/complete/{userId}` - Mark course as completed
- `POST /api/v1/courses/{courseId}/bookmark/{userId}` - Bookmark course
- `DELETE /api/v1/courses/{courseId}/bookmark/{userId}` - Unbookmark course
- `GET /api/v1/courses/user/{userId}/enrolled` - Get user's enrolled courses

### AI-Powered Features

- `POST /api/v1/courses/generate/learning_path/{userId}` - Generate course from learning path
- `POST /api/v1/courses/generate/learning_path/{userId}/confirm` - Confirm generated course
- `POST /api/v1/courses/generate/prompt` - Generate response from prompt
- `POST /api/v1/courses/crawl/url` - Crawl web URL for content

### Health & Monitoring

- `GET /api/v1/courses/health` - Service health check
- `GET /actuator/health` - Spring Boot health endpoint
- `GET /actuator/prometheus` - Prometheus metrics

### Documentation

- `GET /docs` - Swagger UI documentation
- `GET /course-openapi.yaml` - OpenAPI specification

## Data Models

### Course Entity

```java
public class Course {
    private String id;
    private String title;
    private String description;
    private String instructor; // "AI" or user ID
    private List<String> skills;
    private List<Module> modules;
    private List<EnrolledUserInfo> enrolledUsers;
    private Integer numberOfEnrolledUsers;
    private List<String> categories;
    private Level level; // BEGINNER, INTERMEDIATE, ADVANCED
    private String thumbnailUrl;
    private Boolean published;
    private Boolean isPublic;
    private Language language; // EN, ES, FR, etc.
    private double rating;
}
```

### Module Entity

```java
public class Module {
    private String id;
    private String title;
    private String description;
    private List<Lesson> lessons;
    private Integer order;
}
```

### Lesson Entity

```java
public class Lesson {
    private String id;
    private String title;
    private String description;
    private List<LessonContent> content;
    private Integer order;
    private Integer estimatedDuration; // in minutes
}
```

### EnrolledUserInfo Entity

```java
public class EnrolledUserInfo {
    private String userId;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private List<String> acquiredSkills;
    private Map<String, Integer> moduleProgress; // moduleId -> progress percentage
}
```

### DTOs (Data Transfer Objects)

- **CourseRequest**: Course creation data
- **CourseUpdateRequest**: Course update data
- **CourseResponse**: Course response data
- **CourseSummaryResponse**: Course summary for listings
- **LearningPathRequest**: AI course generation request
- **EnrolledUserInfoResponse**: User enrollment information

## Configuration

### Environment Variables

```bash
# Service Configuration
SERVER_PORT_COURSES=8083

# MongoDB Configuration
MONGODB_DATABASE=skillforge
MONGO_URL=mongodb://localhost:27017/skillforge

# JWT Configuration
JWT_SECRET=your-secret-key-here

# User Service Integration
SERVER_HOST_USER=localhost
SERVER_PORT_USER=8082

# GenAI Service Integration (Ensure it is running)
SERVER_HOST_GENAI=localhost
SERVER_PORT_GENAI=8888
```

### Application Properties

```yaml
server:
  port: ${SERVER_PORT_COURSES:8083}
  address: "0.0.0.0"

spring:
  data:
    mongodb:
      database: ${MONGODB_DATABASE:skillforge}
      uri: ${MONGO_URL:mongodb://localhost:27017/skillforge}

jwt:
  secret: ${JWT_SECRET:default-secret-key}

user:
  service:
    uri: http://${SERVER_HOST_USER:localhost}:${SERVER_PORT_USER:8082}
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

# 2. Start User Service (required for course enrollment operations)
# In the user service directory:
./gradlew bootRun

# 3. Set environment variables (optional - defaults are provided)
export SERVER_PORT_COURSES=8083
export MONGODB_DATABASE=skillforge
export MONGO_URL=mongodb://localhost:27017/skillforge
export JWT_SECRET=your-secret-key-here
export SERVER_HOST_USER=localhost
export SERVER_PORT_USER=8082

# 4. Start the course service
./gradlew bootRun

# Or with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Testing

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests CourseServiceTest

# Run with coverage
./gradlew test jacocoTestReport
```

### API Documentation

Once the service is running, access the API documentation:

#### Direct Access (when running course service standalone)

- **Swagger UI**: http://localhost:8083/docs
- **OpenAPI Spec**: http://localhost:8083/course-openapi.yaml (This will download the OpenAPI spec file)

#### Through Gateway (when running with gateway)

- **Swagger UI**: http://localhost:8081/api/v1/courses/docs
- **OpenAPI Spec**: http://localhost:8081/api/v1/courses/user-openapi.yaml (This will download the OpenAPI spec file)

### Database Operations

```bash
# Connect to MongoDB
mongosh mongodb://localhost:27017/skillforge

# View collections
show collections

# Query courses
db.courses.find()

# Query specific course
db.courses.findOne({title: "Introduction to Java"})

# Query by category
db.courses.find({categories: "Programming"})
```

## Monitoring and Logging

### Logging Configuration

```yaml
logging:
  level:
    com.gitittogether.skillforge.server.course: DEBUG
    org.springframework.security: DEBUG
    org.springframework.data.mongodb: DEBUG
```

### Health Checks

- **Service Health**: `GET /api/v1/courses/health`
- **Spring Boot Health**: `GET /actuator/health`
- **Database Connectivity**: Included in health checks
- **User Service Connectivity**: Monitored for inter-service communication

### Metrics

- **Prometheus Metrics**: `GET /actuator/prometheus`
- **Application Metrics**: Request counts, response times, error rates
- **Database Metrics**: Connection pool, query performance
- **AI Generation Metrics**: Course generation success rates and performance

## Security Best Practices

### Implemented Security Measures

1. **JWT Security**
    - Token validation on protected endpoints
    - User ID extraction and validation
    - Secure error responses without information leakage

2. **Input Validation**
    - Request DTO validation with Bean Validation
    - SQL injection prevention (MongoDB)
    - XSS protection through proper encoding

3. **CORS Configuration**
    - Environment-specific CORS policies
    - Proper preflight request handling
    - Secure header configuration

4. **Inter-Service Security**
    - Service key authentication for internal operations
    - Secure user service communication
    - Proper error handling for service failures

5. **AI Integration Security**
    - Secure GenAI service communication
    - Input sanitization for AI prompts
    - Rate limiting for AI generation endpoints

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

3. **User Service Communication Errors**
    - Ensure user service is running
    - Check user service host/port configuration
    - Verify service key authentication

4. **AI Generation Failures**
    - Check GenAI service connectivity
    - Verify AI service configuration
    - Monitor AI service logs for errors

5. **CORS Errors**
    - Verify CORS configuration for environment
    - Check allowed origins and methods
    - Ensure proper preflight request handling

### Debug Mode

Enable debug logging for troubleshooting:

```yaml
logging:
  level:
    com.gitittogether.skillforge.server.course: DEBUG
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

### Inter-Service Issues

```bash
# Test user service connectivity
curl -H "X-Service-Key: course-service-key" \
     http://localhost:8082/api/v1/users/health

# Check service communication logs
tail -f logs/course-service.log | grep "user-service"
```

## Integration with Other Services

### Gateway Integration

- Receives requests through API Gateway
- JWT tokens validated by gateway
- User ID injected via `X-User-Id` header

### User Service Integration

- Inter-service communication via service keys
- Course enrollment, bookmarking, and completion tracking
- User progress synchronization

### GenAI Service Integration

- AI-powered course generation
- Content extraction and enhancement
- Intelligent learning path creation

### Monitoring Integration

- Prometheus metrics for monitoring
- Health checks for load balancers
- Structured logging for log aggregation

## AI-Powered Features

### Course Generation

- **Learning Path Analysis**: Analyzes user skills and goals
- **Content Creation**: Generates course structure and content
- **Skill Mapping**: Maps course content to specific skills
- **Personalization**: Tailors content to user preferences

### Web Crawling

- **Content Extraction**: Extracts relevant content from URLs
- **Data Processing**: Processes and structures extracted content
- **Quality Assurance**: Validates and filters extracted content

### Intelligent Recommendations

- **Skill-Based**: Recommends courses based on user skills
- **Progress-Based**: Suggests next steps based on completion
- **Popularity-Based**: Recommends trending and popular courses 