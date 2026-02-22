# --------- Stage 1: Build Stage ---------
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml first (for dependency caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests


# --------- Stage 2: Runtime Stage ---------
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy JAR from build stage
COPY --from=builder /app/target/*.jar app.jar

# Render provides PORT environment variable
ENV PORT=8080

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]