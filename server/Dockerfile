# Build stage:
FROM gradle:8.14-jdk-21 AS build

WORKDIR /app

# ARG to pass app name into the build (optional fallback)
ARG APP_NAME=skill-forge-server

# Copy gradle files
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle

# Copy source files
COPY src ./src

# Build the application
RUN gradle bootJar --no-daemon

# Run stage:
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Reuse the same ARG in runtime stage
ARG APP_NAME=skill-forge-server
ENV APP_NAME=${APP_NAME}

# Copy the jar file from the build stage
COPY --from=build /app/build/libs/${APP_NAME}.jar app.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
