# Lesson 23: Health Checks / Readiness & Liveness Probes

## What We Learned
This lesson covers **health checks** in Kubernetes, specifically **Readiness Probes** and **Liveness Probes**. We'll learn how to configure these probes to create more robust and resilient deployments.

## Why Do We Need This?
By default, Kubernetes only knows that a container has *started*. It doesn't know if the application *inside* the container is actually healthy and ready to serve traffic. This can lead to two problems:
1.  **Sending traffic to unready Pods**: During a rolling update, Kubernetes might start sending traffic to a new Pod as soon as the container starts, but before the application has finished initializing (e.g., connecting to the database, warming up caches). This can result in errors for users.
2.  **Not restarting frozen Pods**: An application might freeze, get deadlocked, or enter a state where it's running but unable to serve requests. Kubernetes wouldn't know anything is wrong and would leave the broken Pod running.

**Readiness Probes** and **Liveness Probes** solve these problems.

### Liveness Probe
A **Liveness Probe** checks if your application is still alive. If the liveness probe fails, Kubernetes will kill the container and restart it. This helps recover from deadlocks and other frozen states.
**"Is my application running correctly? If not, restart it."**

### Readiness Probe
A **Readiness Probe** checks if your application is ready to accept traffic. If the readiness probe fails, Kubernetes will not kill the container, but it will remove the Pod from the Service's endpoint list. This means no new traffic will be sent to the Pod until the probe succeeds again. This is crucial for zero-downtime rolling updates.
**"Is my application ready for new traffic? If not, stop sending it requests."**

## Project Implementation
Spring Boot Actuator provides a `/actuator/health` endpoint out of the box, which is perfect for our probes. We just need to add the `spring-boot-starter-actuator` dependency to our `pom.xml`.

We will now add liveness and readiness probes to our `user-service` Deployment.

**`k8s/user-service-deployment.yaml` (updated `containers` section)**
```yaml
# ... inside the containers array ...
- name: user-service
  image: user-service:latest
  # ... ports, env, etc.
  readinessProbe:
    httpGet:
      path: /actuator/health/readiness # Specific endpoint for readiness
      port: 8081
    initialDelaySeconds: 15 # Wait 15s before the first probe
    periodSeconds: 10       # Probe every 10s
    failureThreshold: 3     # Consider it failed after 3 consecutive failures
  livenessProbe:
    httpGet:
      path: /actuator/health/liveness # Specific endpoint for liveness
      port: 8081
    initialDelaySeconds: 30 # Wait 30s before the first probe
    periodSeconds: 15       # Probe every 15s
```

**Key Fields:**
-   **`httpGet`**: Defines an HTTP-based probe. You specify the path and port.
-   **`initialDelaySeconds`**: How long to wait after the container starts before performing the first probe. This is important to give your application time to start up.
-   **`periodSeconds`**: How often to perform the probe.
-   **`failureThreshold`**: How many times the probe can fail consecutively before it's considered a failure.

By default, the Spring Boot Actuator health endpoint (`/actuator/health`) is used for both liveness and readiness. However, it's a best practice to enable and use the specific `readiness` and `liveness` probe endpoints, as they provide more control. You can enable them in `application.properties`:
```properties
management.health.probes.enabled=true
```

## How Probes Affect the Pod Lifecycle
-   **Startup**: Container starts. `initialDelaySeconds` for both probes begin.
-   **Becoming Ready**: The readiness probe starts. Once it succeeds, the Pod is marked as `Ready` and the Service starts sending traffic to it.
-   **Staying Alive**: The liveness probe starts. As long as it succeeds, the Pod continues running.
-   **Application Freezes**: The liveness probe fails. After `failureThreshold` is reached, Kubernetes restarts the container.
-   **Application Overloaded**: The application is alive but slow to respond. The readiness probe might fail. Kubernetes stops sending new traffic to the Pod, giving it time to recover. Once the readiness probe succeeds again, the Pod is added back to the service endpoints.

## Commands
-   **Check the status of a Pod with probes:**
    `kubectl get pod <pod-name>`
    Look at the `READY` column. It will show `1/1` if the readiness probe is passing, and `0/1` if it's failing.
-   **See probe events:**
    `kubectl describe pod <pod-name>`
    The `Events` section will show messages related to probe failures (e.g., `Liveness probe failed`, `Readiness probe failed`).

## Important Takeaways
-   **Always configure liveness and readiness probes** for your production applications.
-   **Liveness Probes** restart broken containers.
-   **Readiness Probes** prevent traffic from being sent to unready or overloaded containers.
-   Use the Spring Boot Actuator health endpoints for easy integration.

## Navigation
| Previous Lesson | Main README | Next Lesson |
| :--- | :--- | :--- |
| [Lesson 22](../22-rolling-updates/README.md) | [Main README](../../README.md) | [Lesson 24: Scaling Microservices](../24-scaling-microservices/README.md) |
