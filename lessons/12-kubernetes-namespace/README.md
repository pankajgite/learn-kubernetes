# Lesson 12: Kubernetes Namespace

## What We Learned
This lesson introduces **Namespaces**, a fundamental concept in Kubernetes for organizing and isolating cluster resources. We will create a dedicated namespace for our `team-task-hub` application.

## Why Do We Need This?
Imagine a Kubernetes cluster being used by multiple teams or for multiple projects. If everyone creates resources in the default namespace, you'll quickly run into problems:
-   **Name Collisions**: Two teams might try to create a service with the same name (e.g., `postgres`).
-   **Resource Management**: It's difficult to see which resources belong to which project.
-   **Access Control**: You can't easily grant a team access to only their resources.

**Namespaces** are the solution. They provide a way to partition a single Kubernetes cluster into multiple virtual clusters.

**Key Benefits:**
-   **Organization**: Group resources for a specific application together.
-   **Avoiding Name Conflicts**: Names of resources need to be unique within a namespace, but not across namespaces.
-   **Resource Quotas**: You can set limits on the amount of resources (CPU, memory) that can be used in a namespace.
-   **Access Control**: You can define roles and permissions (RBAC) on a per-namespace basis.

## Project Implementation
We will create a dedicated namespace called `team-task-hub` for all the resources related to our project.

We can do this in two ways:

### 1. Imperative Command (`kubectl`)
This is the quickest way to create a namespace.
```bash
kubectl create namespace team-task-hub
```

### 2. Declarative Manifest (YAML)
This is the recommended approach, as it allows you to store your infrastructure as code. We will create a YAML file to define our namespace.

**`k8s/namespace.yaml`**
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: team-task-hub
```

To apply this manifest:
```bash
kubectl apply -f k8s/namespace.yaml
```

## Working with Namespaces
-   **Creating resources in a namespace:**
    When creating resources, you can specify the namespace using the `-n` or `--namespace` flag.
    ```bash
    kubectl apply -f k8s/user-service-deployment.yaml -n team-task-hub
    ```
    Alternatively, you can add the `namespace` field to the `metadata` section of your YAML files.

-   **Viewing resources in a namespace:**
    ```bash
    # Get all pods in our namespace
    kubectl get pods -n team-task-hub

    # Get all resources in our namespace
    kubectl get all -n team-task-hub
    ```

-   **Setting a default namespace for your context:**
    To avoid typing `-n team-task-hub` for every command, you can change the default namespace for your current `kubectl` context.
    ```bash
    kubectl config set-context --current --namespace=team-task-hub
    ```
    Now, all your `kubectl` commands will be executed against the `team-task-hub` namespace by default.

## Important Takeaways
-   **Namespaces** are used to organize and isolate resources within a Kubernetes cluster.
-   It's a best practice to create a dedicated namespace for each application or environment.
-   Using declarative YAML files for creating namespaces allows you to version-control your cluster setup.

## Navigation
| Previous Lesson | Main README | Next Lesson |
| :--- | :--- | :--- |
| [Lesson 11](../11-minikube-setup/README.md) | [Main README](../../README.md) | [Lesson 13: Deployments & ReplicaSets](../13-deployments-and-replicasets/README.md) |
