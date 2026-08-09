# Lesson 25: Ingress / External Access

## What We Learned
This lesson covers how to expose our services to the outside world using a Kubernetes **Ingress**. We'll learn how an Ingress provides an intelligent way to route external traffic to the correct services within our cluster.

## Why Do We Need This?
So far, our services have been of type `ClusterIP`, meaning they are only reachable from within the Kubernetes cluster. To allow external users to access our application, we need to expose it.

There are a few ways to do this:
-   **`Service` of type `NodePort`**: This exposes the service on a specific port on every node in the cluster. It's simple but not very flexible. You have to use a high-numbered port, and you don't get standard HTTP features like host-based routing.
-   **`Service` of type `LoadBalancer`**: This provisions an external load balancer from a cloud provider (like an AWS ELB). This is a great option for production, but it can be expensive as you typically pay for each LoadBalancer service.
-   **Ingress**: This is the most powerful and flexible method. An Ingress is a Kubernetes object that manages external access to services, typically HTTP. It can provide load balancing, SSL termination, and name-based virtual hosting.

An Ingress itself doesn't do anything. It's a set of rules. You need an **Ingress Controller** running in your cluster to actually implement these rules. The Ingress Controller is a Pod that watches for Ingress objects and configures a reverse proxy (like NGINX, HAProxy, or Traefik) accordingly.

## Architecture / Flow
The Ingress Controller acts as the single entry point for all traffic into the cluster. It inspects the incoming request (e.g., the hostname) and routes it to the appropriate service.

```mermaid
graph TD
    subgraph "External World"
        User
    end

    subgraph "Kubernetes Cluster"
        IC[Ingress Controller (e.g., NGINX)]
        I[Ingress Object (Rules)]
        USVC[User Service]
        TSVC[Task Service]
    end

    User -- "http://api.example.com/users" --> IC
    IC -- "Reads rules from" --> I
    I -- "Defines routing rules" --> IC
    IC -- "/users -> user-service" --> USVC
    IC -- "/tasks -> task-service" --> TSVC
```

## Project Implementation
First, we need to enable the Ingress Controller addon in Minikube. Minikube uses the NGINX Ingress Controller by default.

Next, we create an Ingress object to define our routing rules. We'll route requests starting with `/api/users` to the `user-service` and requests starting with `/api/tasks` to the `task-service`.

**`k8s/ingress.yaml`**
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: app-ingress
  namespace: team-task-hub
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
    - http:
        paths:
          - path: /api/users
            pathType: Prefix
            backend:
              service:
                name: user-service
                port:
                  number: 8081
          - path: /api/tasks
            pathType: Prefix
            backend:
              service:
                name: task-service
                port:
                  number: 8082
```

**Key Fields:**
-   **`kind: Ingress`**: The object type.
-   **`annotations`**: These are used to configure the behavior of the Ingress Controller. `nginx.ingress.kubernetes.io/rewrite-target: /` is a common one needed for NGINX Ingress to correctly route paths.
-   **`spec.rules`**: A list of routing rules.
    -   **`paths`**: Defines the routing for different URL paths.
    -   **`path`**: The URL path to match.
    -   **`pathType: Prefix`**: Matches any path that starts with the specified value.
    -   **`backend`**: Specifies where to send the traffic that matches the path. It points to a Service and a port.

## Commands
-   **Enable the Ingress addon in Minikube:**
    ```bash
    minikube addons enable ingress
    ```
-   **Apply the Ingress object:**
    ```bash
    kubectl apply -f k8s/ingress.yaml
    ```
-   **Get the IP address of the Ingress Controller:**
    The Ingress Controller runs as a service. We need its IP to send requests to it.
    ```bash
    minikube ip
    # This will give you the cluster's IP address.
    ```
    Or, if you are not using the Docker driver:
    ```bash
    kubectl get ingress -n team-task-hub
    # The ADDRESS column will show the IP.
    ```
-   **Test the Ingress:**
    Use `curl` or a browser to send a request to the Minikube IP with the correct path.
    ```bash
    # Get the IP
    MINIKUBE_IP=$(minikube ip)

    # Test the user-service
    curl http://$MINIKUBE_IP/api/users/some-endpoint

    # Test the task-service
    curl http://$MINIKUBE_IP/api/tasks/some-endpoint
    ```

## Important Takeaways
-   **Ingress** is the standard way to manage external HTTP/S traffic to your services.
-   You need an **Ingress Controller** in your cluster to fulfill the Ingress rules.
-   Ingress allows for path-based and host-based routing, acting as a reverse proxy for your microservices.
-   This provides a single, stable entry point for your entire application.

## Navigation
| Previous Lesson | Main README | Next Lesson |
| :--- | :--- | :--- |
| [Lesson 24](../24-scaling-microservices/README.md) | [Main README](../../README.md) | [Lesson 26: Frontend Deployment](../26-frontend-deployment/README.md) |
