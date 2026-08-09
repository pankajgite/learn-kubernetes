# Lesson 15: PersistentVolume & PersistentVolumeClaim

## What We Learned
This lesson introduces the concepts of **PersistentVolumes (PVs)** and **PersistentVolumeClaims (PVCs)**. These are the fundamental Kubernetes objects for managing persistent storage.

## Why Do We Need This?
Containers and Pods are ephemeral. If a Pod running our PostgreSQL database crashes and gets restarted, all the data inside its container's filesystem is lost. This is unacceptable for a database. We need a way to store data that persists independently of the Pod's lifecycle.

Kubernetes provides a powerful storage abstraction to solve this problem:
-   **PersistentVolume (PV)**: A piece of storage in the cluster that has been provisioned by an administrator or dynamically provisioned using Storage Classes. It is a resource in the cluster, just like a CPU or memory. A PV is independent of any individual Pod.
-   **PersistentVolumeClaim (PVC)**: A request for storage by a user. A Pod uses a PVC to get access to a PV. The PVC claims a PV resource, much like a Pod claims CPU and memory resources.

This separation of concerns allows for a clean abstraction between the consumers of storage (the application developers who create PVCs) and the providers of storage (the cluster administrators who create PVs).

## Architecture / Flow
The process works as follows:
1.  A cluster administrator provisions a **PersistentVolume (PV)**. This could be a physical disk, a network share (like NFS), or a cloud storage volume (like an AWS EBS volume).
2.  A developer creates a **PersistentVolumeClaim (PVC)**, requesting a certain amount of storage with specific access modes (e.g., "I need 5GB of storage that can be mounted by one Pod at a time").
3.  Kubernetes **binds** the PVC to a suitable PV.
4.  A Pod is created that references the PVC. The Pod can then mount the volume and read/write data to it.

```mermaid
graph TD
    subgraph "Storage Provisioning (Admin Task)"
        PV[PersistentVolume (e.g., 10GB NFS Share)]
    end

    subgraph "Storage Consumption (Developer Task)"
        PVC[PersistentVolumeClaim (requests 5GB)]
        P[Pod]
    end

    PVC -- "Binds to" --> PV
    P -- "Uses" --> PVC
```

If the Pod `P` dies, a new Pod can be created, claim the same PVC, and get access to the same underlying data on the PV.

## Project Implementation (Local - Minikube)
For local development with Minikube, we don't need to manually create a PV. Minikube comes with a default **StorageClass** called `standard` that can dynamically provision PVs for us. This means we only need to create the PVC.

We will create a PVC for our PostgreSQL database.

**`k8s/postgres-pvc.yaml`**
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: postgres-pvc
  namespace: team-task-hub
spec:
  accessModes:
    - ReadWriteOnce # Can be mounted as read-write by a single node
  resources:
    requests:
      storage: 1Gi # Request 1 GiB of storage
```

**Key Fields:**
-   **`kind: PersistentVolumeClaim`**: The object type.
-   **`spec.accessModes`**: Defines how the volume can be mounted.
    -   `ReadWriteOnce` (RWO): Can be mounted as read-write by a single node. This is common for databases.
    -   `ReadOnlyMany` (ROX): Can be mounted as read-only by many nodes.
    -   `ReadWriteMany` (RWX): Can be mounted as read-write by many nodes.
-   **`spec.resources.requests.storage`**: The amount of storage being requested.

## Commands
-   **Apply the PVC:**
    ```bash
    kubectl apply -f k8s/postgres-pvc.yaml
    ```
-   **Check the PVC and the dynamically created PV:**
    ```bash
    # Check the PVC. It should be in a 'Pending' state until a Pod uses it,
    # or 'Bound' if the StorageClass provisions the PV immediately.
    kubectl get pvc postgres-pvc

    # Check the PVs. You should see a new PV that was created for our PVC.
    kubectl get pv
    ```

## Important Takeaways
-   Data in Pods is ephemeral. **PersistentVolumes** provide durable storage.
-   **PersistentVolumeClaims** are requests for storage made by applications.
-   The **PV/PVC subsystem** decouples applications from the underlying storage technology.
-   **StorageClasses** can be used to dynamically provision PVs when a PVC is created.

## Navigation
| Previous Lesson | Main README | Next Lesson |
| :--- | :--- | :--- |
| [Lesson 14](../14-kubernetes-services-and-dns/README.md) | [Main README](../../README.md) | [Lesson 16: StatefulSets for PostgreSQL](../16-statefulsets-for-postgresql/README.md) |
