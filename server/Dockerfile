# Build stage:
FROM gradle:8.14-jdk21 AS build

WORKDIR /app

# ARG to pass app name into the build (optional fallback)
ARG SERVER_APP_NAME=skill-forge-server

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
ARG SERVER_APP_NAME=skill-forge-server
ENV SERVER_APP_NAME=${SERVER_APP_NAME}

# Copy the jar file from the build stage
COPY --from=build /app/build/libs/${SERVER_APP_NAME}.jar app.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
