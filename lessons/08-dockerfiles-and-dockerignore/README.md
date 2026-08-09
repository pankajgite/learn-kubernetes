# Lesson 08: Dockerfiles & .dockerignore

## What We Learned
In this lesson, we'll write the `Dockerfile` for our Spring Boot microservices. We'll also create a `.dockerignore` file to optimize our image builds.

## Why Do We Need This?
The `Dockerfile` is the blueprint for building our Docker image. It tells Docker exactly what to include in the image and how to configure it. The `.dockerignore` file helps us create smaller and more efficient images by excluding unnecessary files.

## The `Dockerfile`
We will use a multi-stage `Dockerfile`. This is a best practice for building Java applications with Docker.

**Why multi-stage?**
-   **Smaller Images**: The final image contains only the JRE and our application JAR, not the entire JDK and Maven build tools. This significantly reduces the image size.
-   **Better Security**: Fewer tools in the final image mean a smaller attack surface.
-   **Improved Caching**: Docker can cache the dependency download layer, speeding up subsequent builds.

Here is the `Dockerfile` we will use for both `user-service` and `task-service`:

```dockerfile
# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final, smaller image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Explanation:**
-   **Stage 1 (`build`)**:
    -   Starts from a Maven and JDK image.
    -   Copies `pom.xml` and downloads dependencies. This layer is cached, so dependencies are not re-downloaded unless `pom.xml` changes.
    -   Copies the source code.
    -   Builds the application and creates the JAR file.
-   **Stage 2 (final image)**:
    -   Starts from a slim JRE image.
    -   Copies only the JAR file from the `build` stage.
    -   `EXPOSE 8080`: Informs Docker that the container listens on port 8080. Note that for `user-service` and `task-service`, we will override this with the correct port (`8081`, `8082`) in our `docker-compose.yml` or Kubernetes manifests.
    -   `ENTRYPOINT`: Specifies the command to run when the container starts.

## The `.dockerignore` File
To prevent unnecessary files from being copied into our Docker image, we create a `.dockerignore` file in the project root. This works just like `.gitignore`.

```
.git
.idea
target/
*.log
```

This file should be placed in the root directory of each service, or in the project root if the Docker build context is the root.

## Commands
To build a Docker image for the `user-service`:
```bash
# Navigate to the root of the project
cd team-task-hub

# Build the image
docker build -t user-service:latest -f user-service/Dockerfile .
```
*(Note: The `-f` flag specifies the path to the Dockerfile, and `.` sets the build context to the current directory)*

## Important Takeaways
-   Use **multi-stage builds** for smaller, more secure Java application images.
-   A `.dockerignore` file is essential for keeping images lean.
-   The `Dockerfile` is the recipe for creating a portable, containerized version of our service.

## Navigation
| Previous Lesson | Main README | Next Lesson |
| :--- | :--- | :--- |
| [Lesson 07](../07-docker-fundamentals/README.md) | [Main README](../../README.md) | [Lesson 09: Docker Compose](../09-docker-compose/README.md) |
