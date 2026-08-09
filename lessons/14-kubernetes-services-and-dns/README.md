# Lesson 14: Kubernetes Services & DNS

## What We Learned
This lesson covers the **Kubernetes Service** object. We'll learn how Services provide stable networking for our Pods, enabling reliable communication between our microservices. We will create a Service for our `user-service`.

## Why Do We Need This?
Pods in Kubernetes are ephemeral. They can be created, destroyed, and replaced. Each time a Pod is created, it gets a new IP address. This presents a problem: if the `task-service` wants to communicate with the `user-service`, it can't rely on a Pod's IP address because that address will change.

A **Service** solves this problem by providing a stable endpoint for a set of Pods.

**Key Features of a Service:**
-   **Stable IP Address**: A Service gets a stable IP address (called the ClusterIP) that does not change for the lifetime of the Service.
-   **DNS Name**: A Service gets a DNS name within the cluster. This is the most important feature for service-to-service communication. A Service named `my-service` in the `my-ns` namespace will be resolvable at `my-service.my-ns.svc.cluster.local`, or more simply, just `my-service` from within the same namespace.
-   **Load Balancing**: If a Service is backed by multiple Pods (replicas), it will automatically load balance traffic between them.
-   **Decoupling**: Services decouple the consumers of a service from the Pods that provide it.

## Architecture / Flow
The Service uses a **selector** to find all the Pods that belong to it. It then forwards traffic to one of the healthy Pods.

```mermaid
graph TD
    subgraph "Namespace: team-task-hub"
        S[Service: user-service]
        D[Deployment: user-service]
    end

    T[Pod: task-service] -- "http://user-service:8081" --> S

    subgraph "Pods managed by Deployment"
        P1[Pod 1 (app=user-service)]
        P2[Pod 2 (app=user-service)]
    end

    S -- "Selects Pods with label app=user-service" --> P1
    S -- "Selects Pods with label app=user-service" --> P2
    D --> P1
    D --> P2
```
The `task-service` Pod can now reliably connect to the `user-service` using the DNS name `user-service`, without needing to know the individual IP addresses of the `user-service` Pods.

## Project Implementation
We will create a YAML file for the `user-service` Service.

**`k8s/user-service-service.yaml`**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: user-service
  namespace: team-task-hub
spec:
  selector:
    app: user-service # This must match the labels on the Pods
  ports:
    - protocol: TCP
      port: 8081 # The port the service will be available on
      targetPort: 8081 # The port the container is listening on
  type: ClusterIP # The default type, only reachable from within the cluster
```

**Key Fields:**
-   **`kind: Service`**: The object type.
-   **`spec.selector`**: This is the crucial part. The Service will route traffic to any Pod that has the label `app: user-service`. This is why we defined labels in our Deployment's Pod template.
-   **`spec.ports`**: Defines the port mapping.
    -   `port`: The port that other Pods in the cluster will use to talk to this Service.
    -   `targetPort`: The port on the Pod/container that the traffic should be forwarded to.
-   **`spec.type`**: The type of service.
    -   **`ClusterIP`**: (Default) Exposes the service on an internal IP in the cluster. This is what we want for service-to-service communication.
    -   **`NodePort`**: Exposes the service on each Node's IP at a static port.
    -   **`LoadBalancer`**: Exposes the service externally using a cloud provider's load balancer.

## Commands
-   **Apply the Service:**
    ```bash
    kubectl apply -f k8s/user-service-service.yaml
    ```
-   **Check the Service:**
    ```bash
    kubectl get service user-service
    # NAME           TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
    # user-service   ClusterIP   10.108.144.22   <none>        8081/TCP   5s
    ```
    You can see the Service has been assigned a stable `CLUSTER-IP`.

## Important Takeaways
-   **Services** provide stable networking for ephemeral Pods.
-   The link between a Service and Pods is made using **labels and selectors**.
-   Services enable **service discovery** through DNS within the cluster.
-   The `ClusterIP` service type is used for internal, service-to-service communication.

## Navigation
| Previous Lesson | Main README | Next Lesson |
| :--- | :--- | :--- |
| [Lesson 13](../13-deployments-and-replicasets/README.md) | [Main README](../../README.md) | [Lesson 15: PersistentVolume & PersistentVolumeClaim](../15-persistentvolume-and-persistentvolumeclaim/README.md) |
