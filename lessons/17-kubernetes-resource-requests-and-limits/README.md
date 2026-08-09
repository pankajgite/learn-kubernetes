# Lesson 17: Kubernetes Resource Requests & Limits

## What We Learned
This lesson explains how to manage container resources (CPU and memory) in Kubernetes using **requests** and **limits**. We'll update our Deployments to include these important settings.

## Why Do We Need This?
By default, Pods in Kubernetes can consume as much CPU and memory on a node as they want. This can lead to problems:
-   **Resource Starvation**: One greedy application could consume all the resources on a node, starving other applications and potentially causing them to crash.
-   **Node Instability**: If a Pod consumes too much memory, it could cause the entire node to become unstable.
-   **Scheduling Problems**: The Kubernetes scheduler decides where to place Pods. If it doesn't know how many resources a Pod needs, it can't make smart placement decisions. It might place too many demanding Pods on one node, leading to performance issues.

To manage this, Kubernetes allows you to specify resource **requests** and **limits** for each container in a Pod.

-   **Requests**: The amount of resources that Kubernetes guarantees to the container. The scheduler uses the request value to find a node with enough available resources to run the Pod.
-   **Limits**: The maximum amount of resources that the container is allowed to use. If a container exceeds its memory limit, it will be terminated (OOMKilled). If it exceeds its CPU limit, it will be throttled.

## Quality of Service (QoS)
Based on the requests and limits you set, Kubernetes assigns a **Quality of Service (QoS)** class to your Pods.
-   **Guaranteed**: Every container in the Pod has a memory and CPU request and limit, and they are equal. These are the highest priority Pods.
-   **Burstable**: At least one container in the Pod has a memory or CPU request. The Pod is allowed to use more resources than requested, up to its limit (i.e., it can "burst"). This is a common configuration.
-   **BestEffort**: No requests or limits are set. These are the lowest priority Pods and are the first to be killed if the node runs out of resources.

Setting requests and limits is crucial for running stable and predictable applications.

## Project Implementation
We will update our `user-service` Deployment to include resource requests and limits.

**`k8s/user-service-deployment.yaml` (updated `spec.template.spec.containers` section)**
```yaml
# ... inside the containers array ...
- name: user-service
  image: user-service:latest
  imagePullPolicy: IfNotPresent
  ports:
    - containerPort: 8081
  env:
    # ... env vars ...
  resources:
    requests:
      memory: "256Mi"
      cpu: "250m" # 250 millicores (0.25 of a core)
    limits:
      memory: "512Mi"
      cpu: "500m" # 500 millicores (0.5 of a core)
```

**Explanation:**
-   **`resources.requests`**: We are telling Kubernetes that this container needs at least 256 MiB of memory and 0.25 of a CPU core to run. The scheduler will not place this Pod on a node that doesn't have at least this much capacity available.
-   **`resources.limits`**: We are telling Kubernetes that this container should not be allowed to use more than 512 MiB of memory or 0.5 of a CPU core.

With these settings, our `user-service` Pod will be in the **Burstable** QoS class.

## Commands
-   **Apply the updated Deployment:**
    ```bash
    kubectl apply -f k8s/user-service-deployment.yaml
    ```
-   **Check the Pod's resource allocation:**
    You can use `kubectl describe pod <pod-name>` to see the requests and limits applied to the containers within the Pod.
    ```bash
    kubectl describe pod user-service-deployment-xxxx-yyyy
    ```
    Look for the "Resources" section in the output.

## Important Takeaways
-   **Always set resource requests and limits** for your containers to ensure application and cluster stability.
-   **Requests** are for scheduling and guaranteed resources.
-   **Limits** are for preventing resource abuse.
-   Understanding QoS classes helps you predict which Pods will be terminated first under resource pressure.

## Navigation
| Previous Lesson | Main README | Next Lesson |
| :--- | :--- | :--- |
| [Lesson 16](../16-statefulsets-for-postgresql/README.md) | [Main README](../../README.md) | [Lesson 18: Kubernetes Troubleshooting](../18-kubernetes-troubleshooting/README.md) |
