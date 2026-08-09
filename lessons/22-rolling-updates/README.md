# Lesson 22: Rolling Updates

## What We Learned
This lesson explores one of the most powerful features of Kubernetes Deployments: **rolling updates**. We'll learn how Kubernetes allows us to update our applications to a new version with zero downtime.

## Why Do We Need This?
In a traditional deployment, updating an application often required taking the application offline, deploying the new version, and then bringing it back online. This resulted in downtime for users.

Modern applications require high availability, even during updates. A **rolling update** strategy achieves this by incrementally replacing old versions of Pods with new ones.

**How it works:**
1.  You update the Pod template in your Deployment object (e.g., by changing the container image to a new version).
2.  Kubernetes notices the change and starts the rolling update.
3.  It creates a new ReplicaSet for the new version.
4.  It then gradually scales up the new ReplicaSet (adding new Pods) and scales down the old ReplicaSet (terminating old Pods) at the same time.
5.  The Service object automatically starts sending traffic to the new Pods as soon as they are ready, while still sending traffic to the old Pods that haven't been terminated yet.

This ensures that there are always healthy Pods available to serve traffic throughout the update process.

## Architecture / Flow

```mermaid
graph TD
    subgraph "Before Update"
        S[Service] --> P1_v1[Pod v1]
        S --> P2_v1[Pod v1]
        S --> P3_v1[Pod v1]
    end

    subgraph "During Update"
        S_mid[Service] --> P1_v1_mid[Pod v1]
        S_mid --> P2_v1_mid[Pod v1]
        S_mid --> P1_v2_mid[Pod v2 (New)]
    end

    subgraph "After Update"
        S_after[Service] --> P1_v2[Pod v2]
        S_after --> P2_v2[Pod v2]
        S_after --> P3_v2[Pod v2]
    end
```

## Configuring the Rolling Update Strategy
You can fine-tune the rolling update process in your Deployment manifest using the `strategy` field.

**`k8s/user-service-deployment.yaml` (updated `spec` section)**
```yaml
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1 # How many pods can be unavailable during the update.
      maxSurge: 1       # How many extra pods can be created during the update.
  # ... rest of the spec
```

**Key Fields:**
-   **`type: RollingUpdate`**: This is the default strategy. The other option is `Recreate`, which terminates all old Pods before creating new ones (causing downtime).
-   **`maxUnavailable`**: The maximum number of Pods that can be unavailable during the update. Can be an absolute number (e.g., `1`) or a percentage (e.g., `25%`).
-   **`maxSurge`**: The maximum number of Pods that can be created *above* the desired `replicas` count. Can also be a number or a percentage.

With `replicas: 3`, `maxUnavailable: 1`, and `maxSurge: 1`, Kubernetes will:
1.  Create 1 new Pod (total Pods = 4, which is `replicas + maxSurge`).
2.  Wait for the new Pod to be ready.
3.  Terminate 1 old Pod (total Pods = 3).
4.  Repeat this process until all Pods are updated.
At any given time, at least 2 Pods (`replicas - maxUnavailable`) will be available to serve traffic.

## Project Implementation
Let's perform a rolling update.

1.  **Make a code change** in the `user-service` application.
2.  **Build a new Docker image** with a new tag.
    ```bash
    # Make sure you are in the minikube docker env
    eval $(minikube -p minikube docker-env)

    # Build and tag the new image
    docker build -t user-service:v2 -f user-service/Dockerfile .
    ```
3.  **Update the Deployment YAML** to use the new image tag.
    ```yaml
    # in user-service-deployment.yaml
    spec:
      template:
        spec:
          containers:
            - name: user-service
              image: user-service:v2 # Changed from :latest or :v1
    ```
4.  **Apply the updated manifest.**
    ```bash
    kubectl apply -f k8s/user-service-deployment.yaml
    ```

## Commands
-   **Watch the update process:**
    ```bash
    kubectl rollout status deployment/user-service-deployment
    ```
-   **Check the history of a rollout:**
    ```bash
    kubectl rollout history deployment/user-service-deployment
    ```
-   **Roll back to a previous version:**
    If you find a bug in the new version, you can quickly roll back.
    ```bash
    kubectl rollout undo deployment/user-service-deployment
    ```

## Important Takeaways
-   **Rolling updates** are the default and recommended way to update applications in Kubernetes.
-   They allow for **zero-downtime deployments**.
-   You can control the update process with `maxUnavailable` and `maxSurge`.
-   Kubernetes provides simple commands to monitor and even undo rollouts.

## Navigation
| Previous Lesson | Main README | Next Lesson |
| :--- | :--- | :--- |
| [Lesson 21](../21-kubernetes-service-to-service-communication/README.md) | [Main README](../../README.md) | [Lesson 23: Health Checks / Readiness & Liveness Probes](../23-health-checks-readiness-liveness-probes/README.md) |
