# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-23 AS builder

WORKDIR /app

# Copy only pom.xml to cache dependencies
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build jar, skip tests for faster build (remove -DskipTests if you want tests)
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM openjdk:23-jdk AS runner

WORKDIR /app

# Copy jar from builder
COPY --from=builder /app/target/task-service-0.0.1-SNAPSHOT.jar ./app.jar

# Expose port your app listens on (change to 6000)
EXPOSE 6000

ENTRYPOINT ["java", "-jar", "app.jar"]
