# Lesson 11: Minikube Setup

## What We Learned
This lesson guides you through setting up **Minikube**, a tool that lets you run a single-node Kubernetes cluster on your local machine. This is perfect for learning Kubernetes and for local development and testing.

## Why Do We Need This?
Setting up a full multi-node Kubernetes cluster is complex. Minikube provides a simple way to get a functional Kubernetes cluster up and running in minutes. It runs a virtual machine (or a Docker container) on your laptop that acts as a single-node cluster, containing both the master and worker components.

This allows us to interact with a real Kubernetes API and deploy our applications just as we would on a production cluster, but without the overhead of managing a full-blown cluster.

## Installation
To use Minikube, you need to have a hypervisor (like VirtualBox, Hyper-V, or Docker) installed. The installation process for Minikube itself is straightforward.

1.  **Install a Hypervisor**: Docker is a popular choice. Make sure Docker Desktop is running.
2.  **Install Minikube**: Follow the official installation guide for your operating system: [https://minikube.sigs.k8s.io/docs/start/](https://minikube.sigs.k8s.io/docs/start/)
3.  **Install `kubectl`**: This is the command-line tool for interacting with the Kubernetes cluster. Minikube often includes it, or you can install it separately: [https://kubernetes.io/docs/tasks/tools/install-kubectl/](https://kubernetes.io/docs/tasks/tools/install-kubectl/)

## Commands

-   **Start a cluster:**
    This command downloads the necessary images and starts the single-node Kubernetes cluster.
    ```bash
    minikube start
    ```
    To use the Docker driver, you can specify it:
    ```bash
    minikube start --driver=docker
    ```

-   **Check the status:**
    ```bash
    minikube status
    ```

-   **Interact with the cluster:**
    Once Minikube is running, you can use `kubectl` to interact with it.
    ```bash
    kubectl get nodes
    # NAME       STATUS   ROLES           AGE   VERSION
    # minikube   Ready    master,control-plane   5m    v1.23.3
    ```

-   **Using the Docker daemon inside Minikube:**
    This is a crucial step for local development. To deploy images to Minikube, the cluster needs access to them. Instead of pushing your images to a public registry, you can build them directly into Minikube's internal Docker registry.
    
    Run this command in your terminal. It points your local Docker client to the Docker daemon inside the Minikube cluster.
    ```bash
    # For Linux/macOS
    eval $(minikube -p minikube docker-env)

    # For Windows PowerShell
    & minikube -p minikube docker-env | Invoke-Expression
    ```
    After running this, any `docker build` command will build the image inside Minikube, making it immediately available to the cluster.

-   **Stop the cluster:**
    ```bash
    minikube stop
    ```

-   **Delete the cluster:**
    ```bash
    minikube delete
    ```

## Important Takeaways
-   **Minikube** is for running a local, single-node Kubernetes cluster.
-   It's essential for developing and testing applications before deploying them to a production cluster.
-   The `minikube docker-env` command is key to making your locally built Docker images available to the Minikube cluster without needing a remote registry.

## Navigation
| Previous Lesson | Main README | Next Lesson |
| :--- | :--- | :--- |
| [Lesson 10](../10-kubernetes-fundamentals/README.md) | [Main README](../../README.md) | [Lesson 12: Kubernetes Namespace](../12-kubernetes-namespace/README.md) |
