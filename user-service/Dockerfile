# Use a specific Java 21 JRE image
FROM eclipse-temurin:21-jre-jammy

# Set the working directory
WORKDIR /app

# Copy the executable JAR file from the build context
# Assumes the JAR is in the target directory and is the only JAR there.
COPY target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
