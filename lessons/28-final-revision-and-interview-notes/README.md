# Lesson 28: Final Revision & Interview Notes

This final document provides a high-level summary of the key concepts and technologies used in this project, formatted for quick revision and interview preparation.

## Quick Revision

### Microservices
-   **What**: An architectural style that structures an application as a collection of small, independent services.
-   **Why**: Improves scalability, resilience, and development velocity compared to monoliths.
-   **Our Project**: `user-service` and `task-service`.

### Spring Boot
-   **What**: A framework for building stand-alone, production-grade Spring-based applications.
-   **Why**: Simplifies development with auto-configuration, embedded servers, and opinionated defaults.

### Service-to-Service Communication
-   **What**: How microservices talk to each other.
-   **How**: We used **Feign Client**, a declarative REST client, which is cleaner than using `RestTemplate`.

### Docker
-   **What**: A platform for containerizing applications.
-   **Core Concepts**:
    -   `Dockerfile`: The recipe to build an image. We used multi-stage builds for smaller images.
    -   `Image`: A read-only template.
    -   `Container`: A running instance of an image.
-   **Why**: Ensures consistency across environments ("it works on my machine").

### Docker Compose
-   **What**: A tool for defining and running multi-container applications on a single host.
-   **Why**: Great for local development and testing the entire application stack.

### Kubernetes (K8s)
-   **What**: A container orchestration platform for automating deployment, scaling, and management.
-   **Why**: Essential for running applications in production with high availability and scalability.

#### Key Kubernetes Objects
-   **Pod**: The smallest deployable unit; a wrapper around one or more containers. Ephemeral.
-   **Deployment**: Manages stateless applications (like our backend services). Provides self-healing and rolling updates.
-   **StatefulSet**: Manages stateful applications (like our database). Provides stable network IDs and storage.
-   **Service**: Provides a stable network endpoint (IP address and DNS name) for a set of Pods. Enables service discovery.
    -   `ClusterIP`: Internal-only service.
    -   `NodePort`: Exposes service on a port on each node.
    -   `LoadBalancer`: Provisions a cloud load balancer.
-   **Ingress**: Manages external HTTP/S access to services. Provides routing, acting as a reverse proxy.
-   **ConfigMap**: For storing non-sensitive configuration data. Decouples config from the application.
-   **Secret**: For storing sensitive data (passwords, API keys). Data is base64 encoded.
-   **PersistentVolume (PV)**: A piece of storage in the cluster.
-   **PersistentVolumeClaim (PVC)**: A request for storage by a Pod.
-   **Horizontal Pod Autoscaler (HPA)**: Automatically scales the number of Pods based on metrics like CPU utilization.

## Interview Questions

**Q: Why did you choose a microservices architecture?**
> A: We chose microservices to achieve better scalability and agility. It allows us to develop, deploy, and scale the user management and task management functionalities independently. For example, if we expect high traffic on the task service, we can scale it up without affecting the user service.

**Q: How do your services communicate with each other?**
> A: Our services communicate over REST APIs. In the `task-service`, we used Spring Cloud's Feign Client to declaratively define a client for the `user-service`. This is cleaner than manually using `RestTemplate`. In Kubernetes, this communication is enabled by Services, which provide a stable DNS name for each microservice.

**Q: How did you manage configuration for your services? What about sensitive data?**
> A: We externalized our configuration from the application code. For non-sensitive data, like database URLs or service-to-service URLs, we used Kubernetes ConfigMaps. For sensitive data, such as database passwords and JWT secrets, we used Kubernetes Secrets, which store the data encoded in base64 and are the standard for handling confidential information.

**Q: How did you run your database in Kubernetes?**
> A: Since databases are stateful applications, we couldn't use a standard Deployment. We used a StatefulSet, which provides stable network identifiers and persistent storage for each replica. The StatefulSet was configured with a `volumeClaimTemplate` to automatically create a unique PersistentVolumeClaim for each database Pod, ensuring that the data persists even if the Pod is restarted.

**Q: How did you ensure your application could be updated without downtime?**
> A: We relied on the rolling update mechanism provided by Kubernetes Deployments. By configuring a `RollingUpdate` strategy with `maxUnavailable` and `maxSurge`, we could ensure that a minimum number of Pods were always available to serve traffic during an update. We also configured Readiness Probes, which was crucial. The Readiness Probe prevents Kubernetes from sending traffic to a new Pod until the application inside it is fully initialized and ready to serve requests.

**Q: How would you handle an increase in traffic to your application?**
> A: We configured a Horizontal Pod Autoscaler (HPA) for our deployments. The HPA monitors the average CPU utilization of the Pods and automatically scales the number of replicas up or down to meet a target utilization that we set. For this to work, we had to enable the Metrics Server in our cluster and set CPU resource requests on our containers.

**Q: How did you expose your application to the outside world?**
> A: We used a Kubernetes Ingress. We deployed an NGINX Ingress Controller to our cluster, which acts as a reverse proxy and the main entry point for all traffic. We then created an Ingress resource with rules to route traffic based on the URL path. For example, requests to `/api/users` were routed to the `user-service`, and requests to `/` were routed to our frontend service.

## Navigation
| Previous Lesson | Main README |
| :--- | :--- |
| [Lesson 27](../27-complete-deployment-architecture/README.md) | [Main README](../../README.md) |
