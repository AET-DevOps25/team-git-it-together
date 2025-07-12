# SkillForge Backend Platform

A secure, scalable microservices platform for online learning with comprehensive rate limiting and network security.

## 🏗️ **Architecture Overview**

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

## 🔧 **Configuration**

### **Environment Variables**

#### **Required**
```bash
# JWT Configuration
JWT_SECRET=your-super-secure-jwt-secret-key-here
JWT_EXPIRATION_MS=86400000

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password

# Rate Limiting Configuration
RATE_LIMIT_REQUESTS_PER_MINUTE=60
RATE_LIMIT_REQUESTS_PER_SECOND=10
RATE_LIMIT_BURST=20
```

#### **MongoDB Configuration (Development)**
```bash
# MongoDB credentials (pre-configured in docker-compose.dev.yml)
MONGODB_USERNAME=skillForge
MONGODB_PASSWORD=password
MONGODB_HOST=localhost
MONGODB_DATABASE=skillforge_users  # or skillforge_courses
```

#### **Optional**
```bash
# Service Ports
SERVER_PORT_GATEWAY=8081
SERVER_PORT_USER=8082
SERVER_PORT_COURSES=8083

# Spring Profiles
SPRING_PROFILES_ACTIVE=dev
```

### **Service Configuration**

#### **API Gateway**
- **Port**: 8081 (public)
- **Rate Limiting**: Redis-based
- **Authentication**: JWT validation
- **Routing**: Service discovery

#### **User Service**
- **Port**: 8082 (internal only)
- **Database**: MongoDB (skillforge_users)
- **Authentication**: JWT issuance
- **Features**: User management, registration, login

#### **Course Service**
- **Port**: 8083 (internal only)
- **Database**: MongoDB (skillforge_courses)
- **Features**: Course management, enrollment, progress tracking


## 🚀 **Quick Start**

### **Prerequisites**
- Java 21+
- Docker & Docker Compose
- Redis (for rate limiting)
- MongoDB

### **Local Development**

#### **1. Start Infrastructure**
```bash
# Start Redis and MongoDB with pre-configured credentials
docker-compose -f docker-compose.dev.yml up -d

# This will start:
# - Redis on port 6379
# - MongoDB on port 27017 with:
#   - Database: skillForge
#   - User: skillForge
#   - Password: PickleR1cK!
```

#### **2. Generate a Secure JWT Secret (Optional)**

If you want to generate a new JWT secret for your environment, use:

```sh
./generate-jwt-secret.sh
```

#### **3. Start Services**
```bash
# Start API Gateway (with rate limiting)
cd skillforge-gateway
./gradlew bootRun

# Start User Service (internal only)
cd ../skillforge-user
./gradlew bootRun

# Start Course Service (internal only)
cd ../skillforge-course
./gradlew bootRun
```

or simply run:
```bash
./start-all-services.sh
```

#### **4. Test the System**

- **Test Health**:

```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

- **Test The Gateway**:

```bash
cd server/testing-scripts
python3 test_gateway.py
```

- **Test rate limiting (already tested in the gateway test)**

```bash
cd server/testing-scripts
chmod +x test-rate-limiting.sh
./test-rate-limiting.sh
```

This will generate a report in the `test-results-<timestamp>` directory and a TESTING_REPORT.md file.


## 📊 **Rate Limiting Configuration**

### **Default Limits (can be overridden - values are just examples)**

| Environment | Requests/Minute | Requests/Second | Burst Limit |
|-------------|----------------|-----------------|-------------|
| Development | 120            | 20              | 50          |
| Production  | 60             | 10              | 20          |

### **Custom Configuration**
```bash
# Set custom rate limits
export RATE_LIMIT_REQUESTS_PER_MINUTE=100
export RATE_LIMIT_REQUESTS_PER_SECOND=15
export RATE_LIMIT_BURST=30
```

### **Rate Limit Response**
```http
HTTP/1.1 429 Too Many Requests
Content-Type: application/json
Retry-After: 60
X-RateLimit-Limit: 60
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1640998800

{
  "error": "Rate Limit Exceeded",
  "message": "Too many requests. Please try again later.",
  "retryAfter": 60,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## 🔒 **Security Architecture**

### **Network Isolation**
- **API Gateway**: Only public-facing service (port 8081)
- **User Service**: Internal network only (no direct access)
- **Course Service**: Internal network only (no direct access)
- **Redis**: Internal network only (rate limiting)
- **MongoDB**: Internal network only (data storage)

### **Authentication Flow**
1. **Client Request** → API Gateway
2. **Rate Limiting Check** → Redis
3. **JWT Validation** → Gateway
4. **Route to Service** → Internal Network
5. **Service Response** → Gateway → Client


## 📈 **Monitoring and Health Checks**

### **Health Endpoints**
```bash
# Gateway health
curl http://localhost:8081/actuator/health

# Service metrics
curl http://localhost:8081/actuator/metrics

# Rate limiting status
redis-cli keys "rate_limit:*"

# MongoDB connection test
mongosh "mongodb://skillForge:PickleR1cK!@localhost:27017/skillForge"
```

### **Logging**
```bash
# Gateway logs
docker logs skillforge-gateway -f

# Rate limiting logs
docker logs skillforge-gateway | grep "Rate limit"

# MongoDB logs
docker logs skillforge-mongo-dev -f

# Redis logs
docker logs skillforge-redis-dev -f
```

## 🧪 **Testing**

### **Rate Limiting Tests**
```bash
# Run comprehensive rate limiting tests
./test-rate-limiting.sh

# Manual testing
for i in {1..25}; do
  curl -X GET http://localhost:8081/api/v1/courses/public # or any other public endpoint
  echo "Request $i"
done
```

## 📚 **Documentation**

- **[Technical Documentation](docs/TECHNICAL_DOCUMENTATION.md)**: Complete backend flow documentation (subject to change)
- **[Rate Limiting Implementation](docs/RATE_LIMITING_IMPLEMENTATION.md)**: Detailed rate limiting and security guide
- **[JWT Troubleshooting](docs/JWT_TROUBLESHOOTING.md)**: Troubleshooting guide for JWT issues
- **[Redis Documentation](docs/REDIS_DOCUMENTATION.md)**: Redis documentation