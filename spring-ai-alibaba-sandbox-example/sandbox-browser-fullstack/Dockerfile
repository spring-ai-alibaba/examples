# Multi-stage build for Spring Boot application
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Install required packages for VNC and browser automation
RUN apk add --no-cache \
    curl \
    wget \
    chromium \
    chromium-chromedriver

# Create non-root user for security
RUN addgroup -g 1000 sandbox && \
    adduser -u 1000 -G sandbox -s /bin/sh -D sandbox

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder --chown=sandbox:sandbox /app/target/*.jar app.jar

# Switch to non-root user
USER sandbox

# Expose ports (application + VNC)
EXPOSE 8080 5900

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
