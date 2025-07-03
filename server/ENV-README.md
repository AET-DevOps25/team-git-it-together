# Environment Variables for All Services

This document describes all environment variables used by the SkillForge microservices (gateway, user, course), their
purpose, default values, and usage. It also explains how to configure your environment for local development, Docker,
and production.

---

## 🔑 **Core Environment Variables**

| Variable Name            | Service(s)            | Description                                                     | Default / Example Value            | Where Used / Notes                    |
|--------------------------|-----------------------|-----------------------------------------------------------------|------------------------------------|---------------------------------------|
| `SPRING_PROFILES_ACTIVE` | all                   | Spring profile to activate                                      | `docker`                           | Controls which config file is loaded  |
| `MONGO_URL`              | user, course          | MongoDB connection URI (includes host, port, and database name) | `mongodb://mongo:27017/skillforge` | Used by both user and course services |
| `JWT_SECRET`             | user, course, gateway | Secret key for signing JWT tokens                               | (required, no default)             | Must be the same for user and gateway |
| `JWT_EXPIRATION_MS`      | user, course, gateway | JWT expiration time in milliseconds                             | `86400000` (1 day)                 |                                       |
| `SERVER_PORT_USER`       | user                  | Port for user service (internal, not exposed in Docker)         | `8082`                             |                                       |
| `SERVER_PORT_COURSE`     | course                | Port for course service (internal, not exposed in Docker)       | `8083`                             |                                       |
| `SERVER_PORT_GATEWAY`    | gateway               | Port for API gateway                                            | `8081`                             | Exposed as `0.0.0.0:8081` in Docker   |
| `SERVER_HOST_USER`       | gateway               | Host for user service (internal)                                | `user-service`                     | Used by gateway to route requests     |
| `SERVER_HOST_COURSE`     | gateway               | Host for course service (internal)                              | `course-service`                   | Used by gateway to route requests     |
| `REDIS_HOST`             | gateway               | Redis host for rate limiting and caching                        | `redis`                            |                                       |
| `REDIS_PORT`             | gateway               | Redis port                                                      | `6379`                             |                                       |

---

## 🗄️ **MongoDB Configuration**

- **Unified URI:**
    - Both user and course services use the same `MONGO_URL` variable.
    - Default is: `mongodb://mongo:27017/skillforge`
    - Must be changed in the Production environment to point to your MongoDB instance.
    - The database name is the last segment of the URI (`skillforge`).
    - Collections for users and courses are created automatically by the services.

- **Local Development:**
    - Use `localhost` instead of `mongo` if running MongoDB outside Docker:
        - `MONGO_URL=mongodb://localhost:27017/skillforge`

- **Production (MongoDB Atlas):**
    - Use your Atlas connection string:
        - `MONGO_URL=mongodb+srv://<user>:<password>@cluster0.mongodb.net/skillforge?retryWrites=true&w=majority`

---

## 🔒 **JWT & Security**

- `JWT_SECRET` **must** be the same for user, course and gateway services.
- Never commit your real secret to version control.
- For local/dev, you can set a default in `.env` or Docker Compose, but override in production.

---

## 🚦 **Ports & Networking**

- Only the gateway exposes a port to the host (`8081`).
- User and course services are only accessible via the gateway.
- All services are on the `skillforge-network` Docker network.

---

## 🩺 **Health Endpoints**

- Each service exposes a health endpoint that pings MongoDB:
    - User:   `GET /api/v1/users/health`
    - Course: `GET /api/v1/courses/health`
    - Gateway: `GET /actuator/health` (Spring Boot default)
- These endpoints return 200 OK if reachable (and MongoDB is connected for user/course).

---

## 📝 **Example simple .env for Local Docker Compose**

Those variables must be set in your `.env` file or Docker Compose environment section for local development:

```dotenv
MONGO_URL=mongodb://mongo:27017/skillforge
JWT_SECRET=your-super-secure-jwt-secret-key-here
```

---

## 🔗 **Service Connections**

- **Gateway** routes all external traffic to user and course services.
- **User/Course** services connect to MongoDB using `MONGO_URL`.
- **Redis** is used by the gateway for rate limiting and caching.
- All services are discoverable by Docker Compose service name (e.g., `mongo`, `redis`).

---

## Environment Variables Summary (All Services)

| Service            | Variable                       | Description               | Example/Default                                          | Required in Prod |
|--------------------|--------------------------------|---------------------------|----------------------------------------------------------|------------------|
| skillforge-course  | SERVER_PORT_COURSES            | HTTP port                 | 8083                                                     | No               |
|                    | MONGO_URL                      | MongoDB connection string | mongodb://user:pass@host:27017/db?authSource=admin       | Yes              |
|                    | JWT_SECRET                     | JWT signing secret        | dev-secret-key-for-development-only-change-in-production | Yes              |
|                    | JWT_EXPIRATION_MS              | JWT expiration (ms)       | 86400000                                                 | No               |
| skillforge-user    | SERVER_PORT_USER               | HTTP port                 | 8082                                                     | No               |
|                    | JWT_SECRET                     | JWT signing secret        | dev-secret-key-for-development-only-change-in-production | Yes              |
|                    | JWT_EXPIRATION_MS              | JWT expiration (ms)       | 86400000                                                 | No               |
|                    | MONGO_URL                      | MongoDB connection string | mongodb://user:pass@host:27017/db?authSource=admin       | Yes              |
| skillforge-gateway | SERVER_PORT_GATEWAY            | HTTP port                 | 8081                                                     | No               |
|                    | REDIS_HOST                     | Redis host                | redis                                                    | Yes              |
|                    | REDIS_PORT                     | Redis port                | 6379                                                     | Yes              |
|                    | SERVER_PORT_USER               | User service port         | 8082                                                     | Yes              |
|                    | SERVER_PORT_COURSES            | Course service port       | 8083                                                     | Yes              |
|                    | JWT_SECRET                     | JWT signing secret        | dev-secret-key-for-development-only-change-in-production | Yes              |
|                    | JWT_EXPIRATION_MS              | JWT expiration (ms)       | 86400000                                                 | No               |
|                    | SPRING_PROFILES_ACTIVE         | Spring profile            | docker                                                   | No               |
|                    | SERVER_HOST_USER               | User service host         | user-service                                             | Yes              |
|                    | SERVER_HOST_COURSE             | Course service host       | course-service                                           | Yes              |
|                    | RATE_LIMIT_REQUESTS_PER_MINUTE | Rate limit (per min)      | 60                                                       | No               |
|                    | RATE_LIMIT_REQUESTS_PER_SECOND | Rate limit (per sec)      | 10                                                       | No               |
|                    | RATE_LIMIT_BURST               | Rate limit burst          | 20                                                       | No               |

### Required Variables for Production

- `MONGO_URL`: MongoDB connection string (required for all services
- `JWT_SECRET`: Secret key for signing JWT tokens (required for user, course, and gateway services)
- `SPRING_PROFILES_ACTIVE`: Set to `prod` for production (default is `docker` for local development)
- `SERVER_HOST_USER`: Host for user service (required for gateway)
- `SERVER_HOST_COURSE`: Host for course service (required for gateway)