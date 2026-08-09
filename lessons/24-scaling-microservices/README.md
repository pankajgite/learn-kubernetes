# Lesson 24: Scaling Microservices

## What We Learned
This lesson covers how to scale our microservices in Kubernetes. We'll look at both **manual scaling** and **autoscaling** using the Horizontal Pod Autoscaler (HPA).

## Why Do We Need This?
One of the primary benefits of a microservices architecture is the ability to scale services independently. If our `user-service` is receiving a lot of traffic, we can increase the number of its instances without touching the `task-service`. Kubernetes makes this incredibly easy.

### Manual Scaling
Manual scaling is the simplest approach. You, as the operator, decide when to add or remove Pod replicas based on your monitoring of the application's performance.

### Autoscaling
Manual scaling is fine, but it requires constant monitoring. A much more powerful approach is **autoscaling**. You define a policy, and Kubernetes automatically scales your application in response to real-time metrics.

The **Horizontal Pod Autoscaler (HPA)** is a Kubernetes controller that automatically scales the number of Pods in a Deployment or StatefulSet. It monitors a specific metric (most commonly CPU utilization) and adjusts the `replicas` count to keep the average metric value close to a target you've set.

## Manual Scaling
We've already seen how to do this with the `kubectl scale` command.

```bash
# Scale the user-service deployment to 3 replicas
kubectl scale deployment user-service-deployment --replicas=3
```
You can also achieve the same result by changing the `replicas` field in your `user-service-deployment.yaml` and re-applying it.
```yaml
# in user-service-deployment.yaml
spec:
  replicas: 3
```
```bash
kubectl apply -f k8s/user-service-deployment.yaml
```

## Autoscaling with the Horizontal Pod Autoscaler (HPA)
To use the HPA, two things are required:
1.  **Metrics Server**: The HPA needs a source for the metrics it uses to make scaling decisions. The **Metrics Server** is a cluster add-on that collects resource metrics (CPU and memory) from each node and provides them to the Kubernetes API. Minikube often comes with it, but you may need to enable it.
2.  **Resource Requests**: The HPA relies on CPU and memory *requests*. If you haven't set a `requests.cpu` value for your containers, the HPA will not be able to function. We did this in Lesson 17.

### Creating an HPA
We can create an HPA declaratively with a YAML file.

**`k8s/user-service-hpa.yaml`**
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: user-service-hpa
  namespace: team-task-hub
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: user-service-deployment # The deployment to scale
  minReplicas: 2
  maxReplicas: 5
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 80 # Target average CPU utilization across all pods
```

**Key Fields:**
-   **`scaleTargetRef`**: Points to the Deployment that the HPA should manage.
-   **`minReplicas` / `maxReplicas`**: The minimum and maximum number of replicas the HPA can scale to.
-   **`metrics`**: The metrics to monitor. Here, we're telling the HPA to monitor the CPU.
-   **`target.averageUtilization`**: The target value. The HPA will add or remove Pods to try to keep the average CPU utilization across all `user-service` Pods at 80% of their requested CPU.

## Commands
-   **Enable Metrics Server in Minikube:**
    ```bash
    minikube addons enable metrics-server
    ```
-   **Apply the HPA:**
    ```bash
    kubectl apply -f k8s/user-service-hpa.yaml
    ```
-   **Check the HPA status:**
    ```bash
    kubectl get hpa user-service-hpa
    # NAME               REFERENCE                          TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
    # user-service-hpa   Deployment/user-service-deployment   <unknown>/80%   2         5         2          15s
    ```
    The `TARGETS` column will initially show `<unknown>` and then update as the Metrics Server provides data. It will show the current average utilization vs. the target.

-   **Generate load to test the HPA:**
    You can use a load testing tool or even a simple `while` loop to hit an endpoint on your service and watch the HPA kick in.
    ```bash
    # Expose the service temporarily to generate load
    kubectl port-forward svc/user-service 8081:8081

    # In another terminal, run a loop
    while true; do curl http://localhost:8081/some-cpu-intensive-endpoint; done
    ```
    After a few minutes, `kubectl get hpa` should show the `REPLICAS` count increasing.

## Important Takeaways
-   **Manual scaling** is simple but requires operator intervention.
-   The **Horizontal Pod Autoscaler (HPA)** provides automatic, metric-driven scaling.
-   The most common metric for autoscaling is **CPU utilization**.
-   For the HPA to work, you must have the **Metrics Server** enabled and **CPU requests** set on your containers.

## Navigation
| Previous Lesson | Main README | Next Lesson |
| :--- | :--- | :--- |
| [Lesson 23](../23-health-checks-readiness-liveness-probes/README.md) | [Main README](../../README.md) | [Lesson 25: Ingress / External Access](../25-ingress-external-access/README.md) |
