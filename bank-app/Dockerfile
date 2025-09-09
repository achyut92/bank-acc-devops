# Step 1: Build the application
FROM gradle:8.10.2-jdk21 AS build

# Copy Gradle wrapper and build files first (for caching)
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Download dependencies
RUN ./gradlew dependencies --no-daemon || return 0

# Copy source code
COPY . .

# Build the JAR
RUN ./gradlew bootJar --no-daemon

# Step 2: Run the application
FROM eclipse-temurin:21-jdk-jammy

# Copy JAR from build stage
COPY --from=build /build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
