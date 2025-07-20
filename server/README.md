# SkillForge Server

## Overview

The SkillForge Server is a microservices architecture built with Spring Boot, consisting of three core services and supporting infrastructure. This repository contains all server-side components of the SkillForge learning platform.

## Architecture

![SkillForge Server Architecture](../docs/architecture/server-arch.png)

## Services

### 1. [API Gateway](./skillforge-gateway/README.md)
- **Port**: 8081
- **Purpose**: Central entry point for all client requests
- **Features**: 
  - Request routing and load balancing
  - JWT authentication and validation
  - Rate limiting with Redis
  - CORS configuration and security
  - API response caching
  - Request/response logging
- **Technology**: Spring Cloud Gateway, Redis, JWT, Spring Security

### 2. [User Service](./skillforge-user/README.md)
- **Port**: 8082
- **Purpose**: User management and authentication
- **Features**: 
  - User registration and authentication
  - Profile management and updates
  - Achievement tracking and badges
  - Course enrollment history
  - Password reset and email verification
  - User statistics and analytics
  - Social features (followers, following)
- **Technology**: Spring Boot, MongoDB, JWT, BCrypt, Spring Security, Spring Data

### 3. [Course Service](./skillforge-course/README.md)
- **Port**: 8083
- **Purpose**: Course management and AI-powered features
- **Features**: 
  - Course creation, editing, and management
  - Content management (lessons, modules, resources)
  - Course search and filtering
  - Enrollment and progress tracking
  - AI-powered course generation
  - Course recommendations
  - Category and tag management
  - Course analytics and statistics
  - Public and private course support
- **Technology**: Spring Boot, MongoDB, GenAI integration, Spring Data, Spring Security

### 4. [GenAI Service](../genai/README.md)
- **Port**: 8888
- **Purpose**: AI-powered course generation and content creation
- **Features**: 
  - AI-powered course generation
  - AI Chatbot
  - Vector search and similarity matching
  - Crawling and embedding of web pages
- **Technology**: Python/FastAPI, LLM integration, Vector databases, Embedding models, RAG (Retrieval-Augmented Generation)

## Infrastructure

### Redis
- **Port**: 6379
- **Purpose**: Rate limiting, caching, and session management
- **Configuration**: 
  - Token bucket algorithm for API rate limiting
  - Session caching for improved performance
  - API response caching to reduce database load
  - Distributed locking for concurrent operations

### MongoDB
- **Port**: 27017
- **Purpose**: Primary data storage and persistence
- **Configuration**: 
  - Document-based storage for users, courses, and progress
  - Indexed collections for fast queries
  - Aggregation pipelines for analytics
  - Data validation and schema enforcement

## Service Communication

- **Client → Gateway**: All external requests go through the API Gateway
- **Gateway → Services**: Routes requests to appropriate microservices
- **Course Service → GenAI Service**: Direct communication for AI-powered features
- **Services → MongoDB**: Data persistence and retrieval
- **Gateway → Redis**: Rate limiting and caching

## Security & Authentication

- **JWT Tokens**: Stateless authentication across all services
- **Rate Limiting**: Redis-based rate limiting at the gateway level
- **Service-to-Service**: Internal communication secured by service keys
- **Public Endpoints**: Health checks and documentation accessible without authentication

## Quick Start

### Prerequisites

- **Java 21+**
- **Gradle 8+**
- **Docker & Docker Compose**
- **Python 3.8+** (for testing and genai service)

> **⚠️ Important Note**: Running the server standalone is not sufficient for full functionality. The course service connects to the **genai service** located in `/genai` for AI-powered course generation features. Make sure to also start the genai service for complete functionality.

### Option 1: Docker Compose (Recommended)

#### Start All Services
```bash
# Start all services with infrastructure
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f
```

#### Start Only Infrastructure
```bash
# Start only MongoDB and Redis (for local development)
docker-compose -f docker-compose.infrastructure.yml up -d

# Check infrastructure status
docker-compose -f docker-compose.infrastructure.yml ps
```

#### Stop Services
```bash
# Stop all services
docker-compose down

# Stop infrastructure only
docker-compose -f docker-compose.infrastructure.yml down

# Stop and remove volumes (data will be lost)
docker-compose down -v
```

### Option 2: Local Development with Gradle

#### Start Infrastructure First
```bash
# Start MongoDB and Redis
docker-compose -f docker-compose.infrastructure.yml up -d

# Verify infrastructure is running
docker-compose -f docker-compose.infrastructure.yml ps
```

#### Start Services Individually

**Terminal 1 - User Service:**
```bash
cd skillforge-user
./gradlew bootRun
```

**Terminal 2 - Course Service:**
```bash
cd skillforge-course
./gradlew bootRun
```

**Terminal 3 - API Gateway:**
```bash
cd skillforge-gateway
./gradlew bootRun
```

## Environment Configuration

### Environment Variables

Create a `.env` file in the server root directory with the following variables:

#### Core Configuration
```bash
# JWT Configuration (MUST be the same for all services)
JWT_SECRET=your-super-secure-jwt-secret-key-here
JWT_EXPIRATION_MS=86400000

# MongoDB Configuration
MONGO_URL=mongodb://localhost:27017/skillforge

# Service Ports
SERVER_PORT_GATEWAY=8081
SERVER_PORT_USER=8082
SERVER_PORT_COURSES=8083
```

#### Rate Limiting Configuration
```bash
# Rate Limiting (Gateway)
RATE_LIMIT_REQUESTS_PER_MINUTE=60
RATE_LIMIT_REQUESTS_PER_SECOND=10
RATE_LIMIT_BURST=20
```

#### Service Hosts (for Docker Compose)
```bash
# Service Hosts (used by Gateway for routing)
SERVER_HOST_USER=user-service
SERVER_HOST_COURSE=course-service
SERVER_HOST_GATEWAY=server-gateway

# GenAI Service (for AI-powered course generation)
GENAI_HOST=genai-service / or localhost
GENAI_PORT=8888
```

#### Redis Configuration
```bash
# Redis (for rate limiting)
REDIS_HOST=redis
REDIS_PORT=6379
```

#### Spring Profiles
```bash
# Spring Profile
SPRING_PROFILES_ACTIVE=docker
```

### Environment Variables by Service

| Service | Variable | Description | Default | Required |
|---------|----------|-------------|---------|----------|
| **All Services** | `JWT_SECRET` | JWT signing secret | - | ✅ Yes |
| **All Services** | `JWT_EXPIRATION_MS` | JWT expiration (ms) | `86400000` | ❌ No |
| **All Services** | `MONGO_URL` | MongoDB connection | `mongodb://localhost:27017/skillforge` | ✅ Yes |
| **All Services** | `SPRING_PROFILES_ACTIVE` | Spring profile | `docker` | ❌ No |
| **User Service** | `SERVER_PORT_USER` | HTTP port | `8082` | ❌ No |
| **Course Service** | `SERVER_PORT_COURSES` | HTTP port | `8083` | ❌ No |
| **Gateway** | `SERVER_PORT_GATEWAY` | HTTP port | `8081` | ❌ No |
| **Gateway** | `REDIS_HOST` | Redis host | `redis` | ❌ No |
| **Gateway** | `REDIS_PORT` | Redis port | `6379` | ❌ No |
| **Gateway** | `SERVER_HOST_USER` | User service host | `user-service` | ❌ No |
| **Gateway** | `SERVER_HOST_COURSE` | Course service host | `course-service` | ❌ No |
| **Course Service** | `GENAI_HOST` | GenAI service host | `genai-service` | ❌ No |
| **Course Service** | `GENAI_PORT` | GenAI service port | `8888` | ❌ No |


### Environment Setup for Different Scenarios

#### Local Development (Gradle)
```bash
# .env file for local development
JWT_SECRET=dev-secret-key-for-development-only-change-in-production
JWT_EXPIRATION_MS=86400000
MONGO_URL=mongodb://localhost:27017/skillforge
REDIS_HOST=localhost
REDIS_PORT=6379
GENAI_HOST=localhost
GENAI_PORT=8888
SPRING_PROFILES_ACTIVE=dev
```

#### Docker Compose (Local)
```bash
# .env file for Docker Compose
JWT_SECRET=your-super-secure-jwt-secret-key-here-change-in-production
JWT_EXPIRATION_MS=86400000
MONGO_URL=mongodb://mongo:27017/skillforge
REDIS_HOST=redis
REDIS_PORT=6379
GENAI_HOST=genai-service
GENAI_PORT=8888
SPRING_PROFILES_ACTIVE=docker
```

#### Production
```bash
# .env file for production
# 🔒 CRITICAL: Use a cryptographically secure secret!
# Generate using: openssl rand -hex 32
JWT_SECRET=your-cryptographically-secure-production-jwt-secret-key-here
JWT_EXPIRATION_MS=86400000
MONGO_URL=mongodb+srv://user:password@cluster.mongodb.net/skillforge
REDIS_HOST=your-redis-host
REDIS_PORT=6379
GENAI_HOST=your-genai-service-host
GENAI_PORT=8888
SPRING_PROFILES_ACTIVE=prod
SERVER_HOST_USER=your-user-service-host
SERVER_HOST_COURSE=your-course-service-host
```

### Important Notes

1. **JWT Secret**: Must be identical across all services (user, course, gateway)
     - **macOS/Linux**: `openssl rand -hex 64`
     - **Windows (PowerShell)**: `[System.Web.Security.Membership]::GeneratePassword(64,0)`
     - **Windows (Command Prompt)**: `powershell -Command "[System.Web.Security.Membership]::GeneratePassword(64,0)"`
     - **Node.js**: `node -e "console.log(require('crypto').randomBytes(64).toString('hex'))"`
     - **Python**: `python -c "import secrets; print(secrets.token_hex(64))"`
     
     **🔒 Security Best Practices:**
     - Generate a **unique secret** for each environment (dev, staging, prod)
     - Use **at least 64 characters** for production secrets
     - **Never commit secrets** to version control
     - Store secrets in **environment variables** or **secure secret management systems**
     - **Rotate secrets regularly** (every 90-180 days)
     - Use **cryptographically secure random generators** (avoid online generators)
    
2. **MongoDB URL**: 
   - Local: `mongodb://localhost:27017/skillforge`
   - Docker: `mongodb://mongo:27017/skillforge`
   - Production: Use your MongoDB Atlas or hosted MongoDB connection string
   - **🔒 Security**: Use authentication and SSL/TLS in production
3. **Service Hosts**: In Docker Compose, services are accessible by their service names
4. **Redis**: Required for rate limiting in the gateway
   - **🔒 Security**: Use Redis authentication in production
5. **Spring Profiles**: Controls which configuration is loaded
   - `dev`: Development configuration with debug logging
   - `docker`: Docker environment configuration
   - `prod`: Production configuration with optimized settings
6. **GenAI Service**: Required for AI-powered course generation
   - Local: `localhost:8888`
   - Docker: `genai-service:8888`
   - Production: Use your hosted GenAI service URL
   - **🔒 Security**: Use HTTPS and API key authentication in production

### Service URLs

| Service | Local Development | Docker Compose |
|---------|------------------|----------------|
| API Gateway | http://localhost:8081 | http://localhost:8081 |
| User Service | http://localhost:8082 | http://localhost:8082 |
| Course Service | http://localhost:8083 | http://localhost:8083 |
| MongoDB | mongodb://localhost:27017 | mongodb://localhost:27017 |
| Redis | localhost:6379 | localhost:6379 |

## API Documentation

### Through Gateway (Recommended)
- **User Service Docs**: http://localhost:8081/api/v1/users/docs
- **Course Service Docs**: http://localhost:8081/api/v1/courses/docs

### Direct Access (Local Development)
- **User Service Docs**: http://localhost:8082/docs
- **Course Service Docs**: http://localhost:8083/docs

## Testing

### Run Gateway Tests
```bash
cd testing-scripts
python test_gateway.py
```

This comprehensive test script:
- Validates all services are running
- Tests authentication and security
- Measures performance and rate limiting
- Generates detailed reports

### Run Individual Service Tests
```bash
# User Service Tests
cd skillforge-user
./gradlew test

# Course Service Tests
cd skillforge-course
./gradlew test

# Gateway Tests
cd skillforge-gateway
./gradlew test
```

## Health Checks

### Service Health Endpoints
- **Gateway**: http://localhost:8081/actuator/health
- **User Service**: http://localhost:8082/api/v1/users/health
- **Course Service**: http://localhost:8083/api/v1/courses/health

### Infrastructure Health
```bash
# MongoDB
mongosh mongodb://localhost:27017/skillforge --eval "db.runCommand('ping')"

# Redis
redis-cli ping
```

## Development Workflow

### 1. Start Infrastructure
```bash
docker-compose -f docker-compose.infrastructure.yml up -d
```

### 2. Start Services (Choose one)
```bash
# Option A: All services with Docker
docker-compose up -d

# Option B: Individual services with Gradle (Ensure you have the environment variables set)
cd skillforge-user && ./gradlew bootRun &
cd skillforge-course && ./gradlew bootRun &
cd skillforge-gateway && ./gradlew bootRun &
```

### 3. Run Tests
```bash
cd testing-scripts
python test_gateway.py
```

### 4. Access Documentation
- Gateway: http://localhost:8081/api/v1/users/docs
- User Service: http://localhost:8082/docs
- Course Service: http://localhost:8083/docs

## Troubleshooting

### Common Issues

#### Services Not Starting
```bash
# Check if ports are available
netstat -tulpn | grep :8081
netstat -tulpn | grep :8082
netstat -tulpn | grep :8083

# Check Docker containers
docker-compose ps
docker-compose logs [service-name]
```

#### Infrastructure Issues
```bash
# Check MongoDB
docker-compose -f docker-compose.infrastructure.yml logs mongo

# Check Redis
docker-compose -f docker-compose.infrastructure.yml logs redis

# Restart infrastructure
docker-compose -f docker-compose.infrastructure.yml restart
```

#### Authentication Issues
```bash
# Check JWT configuration
echo $JWT_SECRET

# Verify user service is responding
curl http://localhost:8082/api/v1/users/health
```

#### Rate Limiting Issues
```bash
# Check Redis connectivity
redis-cli ping

# Verify Redis configuration in gateway
docker-compose logs server-gateway | grep redis
```