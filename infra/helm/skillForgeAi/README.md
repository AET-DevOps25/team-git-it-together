# SkillForge Helm Deployment Guide

## What is being deployed?

This Helm chart deploys a complete **SkillForge AI learning platform** with the following components:

### 🏗️ **Architecture Overview**
- **Frontend**: React-based client application
- **Backend**: Spring Boot microservices (Gateway, User, Course services)
- **AI Services**: Python-based GenAI service with Weaviate vector database
- **Databases**: MongoDB (user data) and Redis (caching/sessions)
- **Infrastructure**: Kubernetes-native deployment with auto-scaling

### 📦 **Deployed Components**

#### **Frontend Layer**
- **Client Service**: React SPA served via Nginx
- **Ingress**: Nginx ingress controller with SSL/TLS termination
- **Domains**: 
  - Frontend: `https://skillforge.student.k8s.aet.cit.tum.de`
  - API: `https://api.skillforge.student.k8s.aet.cit.tum.de`

#### **Backend Services**
- **API Gateway**: Spring Boot gateway with rate limiting and routing
- **User Service**: Authentication, user management, JWT handling
- **Course Service**: Course management and progress tracking
- **GenAI Service**: AI-powered course generation and chat assistance

#### **Data Layer**
- **MongoDB**: Primary database for user data and courses
- **Redis**: Session storage and caching layer
- **Weaviate**: Vector database for AI embeddings and semantic search

#### **Kubernetes Resources**
- **Deployments**: All services deployed as Kubernetes Deployments
- **Services**: ClusterIP services for internal communication
- **Persistent Volume Claims**: Data persistence for databases
- **Horizontal Pod Autoscalers (HPA)**: Auto-scaling based on CPU/Memory usage
- **Ingress**: External access with SSL/TLS
- **Secrets**: Secure storage for sensitive configuration

#### **High Availability Features**
- **Auto-scaling**: HPA configured for all backend services
- **Health checks**: Liveness and readiness probes
- **Rollout strategy**: Recreate strategy for stateful services
- **Resource limits**: CPU and memory constraints for stability

#### **Security & Networking**
- **VPC/ClusterIP**: All services use internal networking
- **Secrets management**: Kubernetes secrets for sensitive data
- **TLS termination**: SSL certificates via cert-manager
- **Rate limiting**: API gateway protection

## Prerequisites

- **Kubernetes cluster** (v1.22+)
- **kubectl** and **Helm** (v3+) installed and configured
- Access to the [GitHub Container Registry](https://ghcr.io) for private images (see below)
- All required secrets (see below)

---

## 1. Authenticate to GitHub Container Registry (Only if images are private)

If your images are private, authenticate Docker and your cluster to pull from `ghcr.io`:

```sh
echo $GITHUB_TOKEN | docker login ghcr.io -u <your-github-username> --password-stdin
```

For Kubernetes, create a registry secret:

```sh
kubectl create secret docker-registry ghcr \
  --docker-server=ghcr.io \
  --docker-username=<your-github-username> \
  --docker-password=<your-github-token> \
  --docker-email=<your-email>
```

Then patch your default service account (or reference this secret in your Helm values):

```sh
kubectl patch serviceaccount default -n <namespace> -p '{"imagePullSecrets": [{"name": "ghcr"}]}'
```

---

## 2. Deploy the Chart

Add the namespace if needed (It is already created in the cluster):

```sh
kubectl create namespace <namespace>
```

Install or upgrade the chart:

```sh
helm upgrade --install skillforge-ai ./infra/helm/skillForgeAi \
  --namespace <namespace> \
  --set image.tag=<image-tag> \
  --set host=<your-domain> \
  --set secrets.mongoDbUrl="<your-mongo-db-url>" \
  --set secrets.mongodbDatabase="<your-mongodb-database>" \
  --set secrets.jwtSecret="<your-jwt-secret>" \
  --set secrets.llmProvider="<your-llm-provider>" \
  --set secrets.openaiApiBase="<your-openai-base-url>" \
  --set secrets.openaiApiKey="<your-openai-api-key>" \
  --set secrets.openaiModel="<your-openai-model>"

```

---

## 3. Verify Deployment

```sh
kubectl get pods -n <namespace>
kubectl get svc -n <namespace>
kubectl get ingress -n <namespace>
kubectl get hpa -n <namespace>
```

You can also access the Client UI at:
```sh
https://skillforge.student.k8s.aet.cit.tum.de/
```

---

## 4. Access the logs

1. Get the pod name:

```sh
kubectl get pods -n <namespace>
```

2. Access the logs:

```sh
kubectl logs -n <namespace> <pod-name>
```

---

## 4. Connect to a pod

1. Get the pod name:

```sh
kubectl get pods -n <namespace>
```

2. Connect to the pod:

```sh
kubectl -n <namespace> exec -it <pod-name> -- /bin/sh
```

---

## 5. Stop and delete the Chart

if you want to stop and delete the chart, you can use the following command:

```sh
helm uninstall skillforge-ai -n <namespace>
```

---

## 6. Notes

- All service images are pulled from `ghcr.io/aet-devops25/team-git-it-together`.
- Update `values.yaml` or use `--set` to override any configuration.


                                    Happy deploying 🚀