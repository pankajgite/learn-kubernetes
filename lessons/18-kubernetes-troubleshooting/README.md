# Lesson 18: Kubernetes Troubleshooting

## What We Learned
This lesson provides a practical guide to troubleshooting common issues in Kubernetes. We'll cover the essential `kubectl` commands for inspecting the state of your cluster and applications.

## Why Do We Need This?
Deploying applications to Kubernetes is a great start, but things can and do go wrong. A Pod might not start, a service might be unreachable, or performance might be poor. Knowing how to debug these issues is a critical skill for any developer working with Kubernetes.

## The Troubleshooting Workflow
A typical troubleshooting session involves a top-down approach:
1.  Check the high-level objects (Deployment, StatefulSet).
2.  Drill down to the Pods.
3.  Inspect the Pods and their containers.
4.  Check the logs.
5.  Inspect the related networking objects (Services).

## Essential `kubectl` Commands

### 1. `kubectl get`
This is your starting point. It gives you an overview of your resources.
```bash
# Get all resources in the current namespace
kubectl get all

# Get all pods and see which node they are on
kubectl get pods -o wide

# Get the YAML definition of a resource
kubectl get pod <pod-name> -o yaml
```
**Look for:**
-   **STATUS**: Is the Pod `Running`, `Pending`, `CrashLoopBackOff`, or `ImagePullBackOff`?
-   **RESTARTS**: A high restart count indicates the container is crashing.

### 2. `kubectl describe`
This command provides a detailed, human-readable view of a resource. It's often the most useful command for finding out *why* something is wrong.
```bash
kubectl describe pod <pod-name>
```
**Look for:**
-   **Events**: At the bottom of the output, there is an `Events` section. This is the most important part for troubleshooting. It will show you events like:
    -   `FailedScheduling`: The scheduler couldn't find a node for the Pod (e.g., due to insufficient CPU/memory).
    -   `ImagePullBackOff`: Kubernetes couldn't pull the container image. (Is the image name correct? Did you run `eval $(minikube docker-env)`?)
    -   `FailedMount`: There was a problem mounting a volume.
    -   `OOMKilled`: The container exceeded its memory limit.
-   **State**: Shows the current state of the container (e.g., `Running`, `Waiting`, `Terminated`). If terminated, it will show the `Reason` and `Exit Code`.

### 3. `kubectl logs`
This command streams the logs from a container. This is essential for debugging application-level issues.
```bash
# Get logs from a pod
kubectl logs <pod-name>

# Follow the logs in real-time
kubectl logs -f <pod-name>

# If a pod has multiple containers, you must specify which one
kubectl logs <pod-name> -c <container-name>

# Get logs from a previous instance of a crashed container
kubectl logs <pod-name> --previous
```

### 4. `kubectl exec`
This command allows you to get a shell inside a running container. This is incredibly useful for inspecting the container's environment, checking file contents, or testing network connectivity from within the Pod.
```bash
# Get a shell inside a container
kubectl exec -it <pod-name> -- /bin/sh

# Once inside, you can use standard Linux tools
# ls -l /app
# cat /app/config/application.properties
# ping user-service
```

## Common Problems and How to Solve Them

-   **Pod is `Pending`**:
    -   **Reason**: Usually a scheduling failure.
    -   **Debug**: `kubectl describe pod <pod-name>`. Look at the `Events`. It will likely say there are not enough resources (CPU/memory).
    -   **Fix**: Adjust the Pod's resource `requests` or add more capacity to your cluster.

-   **Pod is `ImagePullBackOff` or `ErrImagePull`**:
    -   **Reason**: Kubernetes cannot pull the specified image.
    -   **Debug**: `kubectl describe pod <pod-name>`. Check the image name. If using Minikube and a local image, did you build the image *inside* Minikube's Docker daemon (`eval $(minikube docker-env)`)?
    -   **Fix**: Correct the image name in your Deployment YAML or build the image correctly.

-   **Pod is `CrashLoopBackOff`**:
    -   **Reason**: The container starts, then immediately crashes. Kubernetes keeps trying to restart it. This is almost always an application error.
    -   **Debug**: `kubectl logs <pod-name> --previous`. The `--previous` flag is key, as it shows the logs from the last, crashed instance of the container. The logs will contain the application stack trace or error message.
    -   **Fix**: Fix the application code or configuration based on the log output.

-   **Service is not reachable (`Connection refused`)**:
    -   **Reason**: The Service is not correctly selecting the Pods, or the application inside the Pod is not listening on the correct port.
    -   **Debug**:
        1.  `kubectl describe service <service-name>`. Check the `Selector`. Does it match the labels on your Pods?
        2.  Check the `Endpoints`. Does the Service have any endpoints listed? If not, the selector is wrong.
        3.  `kubectl get pod <pod-name> -o yaml`. Check the `containerPort` in the Pod spec.
        4.  `kubectl exec` into the client Pod and try to `ping` or `curl` the service by its DNS name (`curl http://my-service:port`).

## Important Takeaways
-   Master the "big four" troubleshooting commands: `get`, `describe`, `logs`, and `exec`.
-   Always start with `kubectl describe` on the problematic resource. The `Events` section is your best friend.
-   `CrashLoopBackOff` means your application is broken. Check the logs with `kubectl logs --previous`.

## Navigation
| Previous Lesson | Main README | Next Lesson |
| :--- | :--- | :--- |
| [Lesson 17](../17-kubernetes-resource-requests-and-limits/README.md) | [Main README](../../README.md) | [Lesson 19: ConfigMaps](../19-configmaps/README.md) |
