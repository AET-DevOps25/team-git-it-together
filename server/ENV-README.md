# Environment Variables for All Services

This document describes all environment variables used by the SkillForge microservices (gateway, user, course), their
purpose, default values, and usage. It also explains how to configure your environment for local development, Docker,
and production.

---

## 🔑 **Core Environment Variables**

| Variable Name            | Service(s)    | Description                                                     | Default / Example Value            | Where Used / Notes                    |
|--------------------------|---------------|-----------------------------------------------------------------|------------------------------------|---------------------------------------|
| `SPRING_PROFILES_ACTIVE` | all           | Spring profile to activate                                      | `docker`                           | Controls which config file is loaded  |
| `MONGO_URL`              | user, course  | MongoDB connection URI (includes host, port, and database name) | `mongodb://mongo:27017/skillforge` | Used by both user and course services |
| `JWT_SECRET`             | user, gateway | Secret key for signing JWT tokens                               | (required, no default)             | Must be the same for user and gateway |
| `JWT_EXPIRATION_MS`      | user, gateway | JWT expiration time in milliseconds                             | `86400000` (1 day)                 |                                       |
| `SERVER_PORT_USER`       | user          | Port for user service (internal, not exposed in Docker)         | `8082`                             |                                       |
| `SERVER_PORT_COURSE`     | course        | Port for course service (internal, not exposed in Docker)       | `8083`                             |                                       |
| `SERVER_PORT_GATEWAY`    | gateway       | Port for API gateway                                            | `8081`                             | Exposed as `0.0.0.0:8081` in Docker   |
| `REDIS_HOST`             | gateway       | Redis host for rate limiting and caching                        | `redis`                            |                                       |
| `REDIS_PORT`             | gateway       | Redis port                                                      | `6379`                             |                                       |

---

## 🗄️ **MongoDB Configuration**

- **Unified URI:**
    - Both user and course services use the same `MONGO_URL` variable.
    - Example: `mongodb://mongo:27017/skillforge`
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

- `JWT_SECRET` **must** be the same for user and gateway services.
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
- These endpoints return 200 OK if MongoDB is reachable, 503 otherwise.

---

## 📝 **Example .env for Local Docker Compose**

```env
SPRING_PROFILES_ACTIVE=docker
MONGO_URL=mongodb://mongo:27017/skillforge
JWT_SECRET=your-super-secure-jwt-secret-key-here
JWT_EXPIRATION_MS=86400000
SERVER_PORT_USER=8082
SERVER_PORT_COURSE=8083
SERVER_PORT_GATEWAY=8081
REDIS_HOST=redis
REDIS_PORT=6379
```

---

## 🛠️ **Best Practices**

- Always set secrets and sensitive values via environment variables, not in code.
- Use `.env` files for local/dev, and secret managers or CI/CD for production.
- To change the MongoDB database name, update the last segment of `MONGO_URL`.
- For production, use strong, unique secrets and secure your database credentials.

---

## 🔗 **Service Connections**

- **Gateway** routes all external traffic to user and course services.
- **User/Course** services connect to MongoDB using `MONGO_URL`.
- **Redis** is used by the gateway for rate limiting and caching.
- All services are discoverable by Docker Compose service name (e.g., `mongo`, `redis`).

---