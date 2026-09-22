# --- Build ---
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy the pom.xml file to the working directory
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code to the working directory
COPY src ./src

# Compile the application and package it into a .jar file, skipping tests for faster build
RUN mvn clean package -DskipTests

# --- Runtime ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user to run the application for security reasons
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the compiled .jar file from the build stage to this clean image
COPY --from=build /app/target/*.jar app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Set the entry point to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]