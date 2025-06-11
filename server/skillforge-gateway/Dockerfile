# ─── Build Stage ──────────────────
FROM gradle:8.14-jdk21 AS build

WORKDIR /app

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

# Copy only the built jar
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port defined in application.yml
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]