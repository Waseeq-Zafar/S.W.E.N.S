# --------- Builder Stage ---------
FROM maven:3.9.9-eclipse-temurin-23 AS builder

WORKDIR /app

# Copy only pom.xml first to leverage Docker layer caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Build the application
RUN mvn clean package

# --------- Runner Stage ---------
FROM eclipse-temurin:23-jdk AS runner

WORKDIR /app

# Copy the jar from builder stage
COPY --from=builder /app/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

# Expose internal port (e.g., for local container-to-container communication)
EXPOSE 8080
# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
