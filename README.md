# Team Task Hub

A comprehensive project demonstrating the implementation of a microservices-based task management system using Spring Boot, Docker, and Kubernetes.

## Problem Statement

The goal of this project is to build a scalable and resilient task management application. It addresses the challenges of monolithic architectures by breaking down the system into independent, deployable microservices. This approach facilitates easier development, testing, and deployment, while also providing a practical learning path for modern cloud-native technologies.

## Key Features

-   **User Management**: Secure user registration and authentication.
-   **Task Management**: Create, update, delete, and assign tasks.
-   **Microservices Architecture**: Independent services for users and tasks.
-   **Containerization**: Dockerized services for consistent environments.
-   **Orchestration**: Kubernetes for automated deployment, scaling, and management.
-   **Service-to-Service Communication**: RESTful APIs and Feign clients for inter-service communication.

## Technology Stack

-   **Backend**: Java, Spring Boot
-   **Database**: PostgreSQL
-   **Security**: Spring Security, JWT
-   **Containerization**: Docker, Docker Compose
-   **Orchestration**: Kubernetes, Minikube
-   **Build Tool**: Maven

## Project Architecture

The application is designed as a set of communicating microservices.

```mermaid
graph TD
    subgraph "Kubernetes Cluster"
        direction LR
        
        subgraph "Services"
            A[User Service]
            B[Task Service]
        end

        subgraph "Database"
            C[PostgreSQL]
        end
    end

    D[Client] --> A
    D --> B
    B --> A
    A --> C
    B --> C
```

## Folder Structure

```
team-task-hub/
├── .dockerignore
├── .gitignore
├── docker-compose.yml
├── k8s/
│   ├── user-service.yaml
│   └── task-service.yaml
├── pom.xml
├── README.md
├── task-service/
│   ├── Dockerfile
│   └── ...
└── user-service/
    ├── Dockerfile
    └── ...
```

## Microservices

| Service        | Port | Responsibilities                               |
| :------------- | :--- | :--------------------------------------------- |
| **User Service** | 8081 | Manages user accounts, authentication, and profiles. |
| **Task Service** | 8082 | Manages tasks, assignments, and status updates.    |

## Setup and Deployment

### Prerequisites

-   Java 17+
-   Maven
-   Docker
-   Minikube

### Local Development

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd team-task-hub
    ```

2.  **Run each service:**
    Navigate to each service's directory (`user-service`, `task-service`) and run:
    ```bash
    mvn spring-boot:run
    ```

### Docker Compose

1.  **Build and run the services:**
    ```bash
    docker-compose up --build
    ```

### Kubernetes (Minikube)

1.  **Start Minikube:**
    ```bash
    minikube start
    ```

2.  **Apply Kubernetes manifests:**
    ```bash
    kubectl apply -f k8s/
    ```

## 📚 Lessons / Documentation

This project is structured into a series of lessons that guide you through the development and deployment process.

| Lesson | Topic                                           |
| :----- | :---------------------------------------------- |
| 01     | [Project & Microservices Architecture](./lessons/01-project-and-microservices-architecture/README.md)            |
| 02     | [User Service](./lessons/02-user-service/README.md)                                    |
| 03     | [Task Service](./lessons/03-task-service/README.md)                                    |
| 04     | [REST vs Feign / Service-to-Service Communication](./lessons/04-rest-vs-feign/README.md)|
| 05     | [JWT Authentication & Spring Security](./lessons/05-jwt-authentication/README.md)            |
| 06     | [PostgreSQL & Database Design](./lessons/06-postgresql-database-design/README.md)                    |
| 07     | [Docker Fundamentals](./lessons/07-docker-fundamentals/README.md)                             |
| 08     | [Dockerfiles & .dockerignore](./lessons/08-dockerfiles-and-dockerignore/README.md)                     |
| 09     | [Docker Compose](./lessons/09-docker-compose/README.md)                                  |
| 10     | [Kubernetes Fundamentals](./lessons/10-kubernetes-fundamentals/README.md)                         |
| 11     | [Minikube Setup](./lessons/11-minikube-setup/README.md)                                  |
| 12     | [Kubernetes Namespace](./lessons/12-kubernetes-namespace/README.md)                            |
| 13     | [Deployments & ReplicaSets](./lessons/13-deployments-and-replicasets/README.md)                       |
| 14     | [Kubernetes Services & DNS](./lessons/14-kubernetes-services-and-dns/README.md)                       |
| 15     | [PersistentVolume & PersistentVolumeClaim](./lessons/15-persistentvolume-and-persistentvolumeclaim/README.md)        |
| 16     | [StatefulSets for PostgreSQL](./lessons/16-statefulsets-for-postgresql/README.md)                     |
| 17     | [Kubernetes Resource Requests & Limits](./lessons/17-kubernetes-resource-requests-and-limits/README.md)           |
| 18     | [Kubernetes Troubleshooting](./lessons/18-kubernetes-troubleshooting/README.md)                      |
| 19     | [ConfigMaps](./lessons/19-configmaps/README.md)                                      |
| 20     | [Kubernetes Secrets](./lessons/20-kubernetes-secrets/README.md)                              |
| 21     | [Kubernetes Service-to-Service Communication](./lessons/21-kubernetes-service-to-service-communication/README.md)     |
| 22     | [Rolling Updates](./lessons/22-rolling-updates/README.md)                                 |
| 23     | [Health Checks / Readiness & Liveness Probes](./lessons/23-health-checks-readiness-liveness-probes/README.md)     |
| 24     | [Scaling Microservices](./lessons/24-scaling-microservices/README.md)                           |
| 25     | [Ingress / External Access](./lessons/25-ingress-external-access/README.md)                       |
| 26     | [Frontend Deployment](./lessons/26-frontend-deployment/README.md)                             |
| 27     | [Complete Deployment Architecture](./lessons/27-complete-deployment-architecture/README.md)                |
| 28     | [Final Revision & Interview Notes](./lessons/28-final-revision-and-interview-notes/README.md)                |

---

