# ─── Build Stage ──────────────────
FROM gradle:8.4.0-jdk21 AS build

WORKDIR /app

# Use ARG to allow setting the application name at build time
ARG SERVER_APP_NAME=skill-forge-server

# Avoid re-downloading dependencies on code change
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true # Run once to cache dependencies

# Copy source files
COPY src ./src

# Build the JAR
RUN gradle bootJar --no-daemon

# ─── Run Stage ──────────────────────────────
FROM eclipse-temurin:21-jdk-jammy AS runner

WORKDIR /app

# Use same ARG and make it available in container
ARG SERVER_APP_NAME=skill-forge-server
ENV SERVER_APP_NAME=${SERVER_APP_NAME}

# Copy only the built jar
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port defined in application.yml
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
