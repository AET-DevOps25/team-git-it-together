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

#### Monitoring Stack

This chart also deploys a full monitoring stack for SkillForge:

- **Prometheus**: Metrics collection and alerting
- **Alertmanager**: Handles alerts from Prometheus
- **Grafana**: Dashboards and visualization
- **Loki**: Log aggregation
- **Promtail**: Log shipping to Loki
- **Mailhog**: For Alert mails Testing

## Accessing Monitoring Tools

- **Grafana**: [https://grafana.skillforge.student.k8s.aet.cit.tum.de](https://grafana.skillforge.student.k8s.aet.cit.tum.de)
  - Default login: `admin` / password from `values.yaml` (`GF_SECURITY_ADMIN_PASSWORD`)
  - All dashboards are auto-provisioned from `/monitoring/grafana/dashboards` and `/monitoring/grafana-dashboards`.
- **Prometheus**: [https://prometheus.skillforge.student.k8s.aet.cit.tum.de](https://prometheus.skillforge.student.k8s.aet.cit.tum.de)
- **Alertmanager**: [https://alertmanager.skillforge.student.k8s.aet.cit.tum.de](https://alertmanager.skillforge.student.k8s.aet.cit.tum.de)
- **Mailhog**: [https://mailhog.skillforge.student.k8s.aet.cit.tum.de](https://mailhog.skillforge.student.k8s.aet.cit.tum.de) (web UI for captured emails)

## Dashboards & Provisioning

- All dashboards are loaded from a single ConfigMap and mounted at `/var/lib/grafana/dashboards`.
- The provisioning config (`dashboards.yaml`) points to this folder.
- To add a new dashboard:
  1. Place your dashboard JSON in `monitoring/grafana/dashboards` or `monitoring/grafana-dashboards`.
  2. Re-run `helm upgrade` to sync it to the cluster.

## Prometheus Targets

- Prometheus scrapes all core services using their Kubernetes service names (e.g., `skillforge-gateway:8081`).
- If you add a new service, update the Prometheus config in the Helm chart to include its service name and port.

## Logs

- Promtail collects logs from all pods and ships them to Loki.
- You can explore logs in Grafana using the pre-built logs dashboard.

## Alerts

- Alerts are defined in `monitoring/alert.rules.yml` and loaded into Prometheus.
- Alertmanager is configured with a minimal/no-op receiver by default. To enable real alerting, update the Alertmanager config in the Helm chart.

---

For more details, see the `/monitoring` directory and the `values.yaml` for configuration options.

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

## 1.1. Copy Monitoring Config Files

Before deploying, copy the monitoring configuration files into the Helm chart directory so Helm can access them:

```sh
cd skillForgeAi
mkdir -p infra/helm/skillForgeAi/monitoring
cp -r monitoring/prometheus monitoring/loki monitoring/grafana infra/helm/skillForgeAi/monitoring/
# Optional: Remove the img folder from the grafana directory
rm -rf infra/helm/skillForgeAi/monitoring/grafana/img
# Optional: Remove the README.md file from the grafana directory
rm -rf infra/helm/skillForgeAi/monitoring/grafana/README.md
```

This ensures Prometheus, Alertmanager, Loki, and Promtail configs are available for Helm templating.

---
## 1.2 Ensure you are in the right kubernetes context

```sh
kubectl config get-contexts
```

If you are not in the right context, you can switch to the right context with the following command:

```sh
kubectl config use-context <context-name>
```

> Note: in this project we are using a context called `student` which the config file get downloaded from the cluster and then copied to the `~/.kube/config` file.
> DO NOT FOGET TO CREATE A BACKUP OF THE `~/.kube/config` FILE BEFORE RUNNING THE COMMANDS BELOW.


---

## 2. Deploy the Chart

Add the namespace if needed (It is already created in the cluster):

```sh
kubectl create namespace <namespace>
```
Validate the chart:

```sh
helm lint ./infra/helm/skillForgeAi
```

Dry-run the deployment to check for errors:

```sh
helm install --dry-run --debug skillforge-ai ./infra/helm/skillForgeAi \
  --namespace <namespace> \
  --set image.tag=<image-tag> \
  --set host=<your-domain> \
  --set secrets.mongoDbUrl="<your-mongo-db-url>" \
  --set secrets.mongodbDatabase="<your-mongodb-database>" \
  --set secrets.jwtSecret="<your-jwt-secret>" \
  --set secrets.llmProvider="<your-llm-provider>" \
  --set secrets.openaiApiBase="<your-openai-base-url>" \
  --set secrets.openaiApiKey="<your-openai-api-key>" \
  --set secrets.openaiModel="<your-openai-model>" \
  --set monitoring.enabled=true \
  --set hpa.enabled=true
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
  --set monitoring.enabled=true
  --set hpa.enabled=true
```

if you want to disable monitoring or hpa, you can set the following flags to false:

```sh
  --set monitoring.enabled=false
  --set hpa.enabled=false
```

> Note: Monitoring and HPA are enabled by default.

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