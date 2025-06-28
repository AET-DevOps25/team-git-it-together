# 🚀 SkillForge.ai – Local Dev Setup

Provision and run all microservices for SkillForge.ai using Kubernetes, Helmfile, and local secrets.
This guide is for **local development** and assumes you have a Kubernetes cluster accessible (e.g., Minikube, Docker Desktop, or a remote cluster with sufficient permissions).

---

## 📦 Folder Overview

```
helm/
├── helmfile.yaml             # Defines all microservice releases
├── ingress.yaml              # Ingress routes to all services
├── client/                   # Helm chart: client frontend
├── gateway/                  # Helm chart: API gateway
├── user/                     # Helm chart: user service
├── genai/                    # Helm chart: genai service
└── ...
```

---

## ⚡ Quick Start (Local Dev)

### 1. **Clone the Repo**

```bash
git clone https://github.com/AET-DevOps25/team-git-it-together.git
cd team-git-it-together/infra/helm/skill-forge-ai
```


### 2. **Create Local Development Namespace**

> You can use a namespace like `skillforge-dev` for isolation:

```bash
kubectl create namespace skillforge-dev
```

> **⚠️ Important:**
> Always check your current Kubernetes context before running cluster commands!
> Run:
>
> ```bash
> kubectl config current-context
> ```
>
> Make sure it matches your **local cluster** (like `minikube`, `docker-desktop`, or `kind-...`).
> If not, switch context:
>
> ```bash
> kubectl config use-context <local-context-name>
> ```
>
> This helps avoid accidentally modifying a remote or production cluster.

### 🛡️ How to Ensure You're Using Your Local Cluster/Context

#### 1. **Check Current Context**

Show the current kubectl context:

```bash
kubectl config current-context
```

* Typical values for local dev:

  * `minikube`
  * `docker-desktop`
  * `kind-<cluster-name>`

#### 2. **List All Contexts**

See all available contexts:

```bash
kubectl config get-contexts
```

* Your current one will have a `*` in the first column.

#### 3. **Switch Context (if needed)**

If you’re **not** on your local context, switch:

```bash
kubectl config use-context minikube
# or
kubectl config use-context docker-desktop
```

Replace with your local cluster context name.

#### 4. **Verify Cluster Info**

Optional, to see the API server endpoint:

```bash
kubectl cluster-info
```

* Local clusters often have endpoints like `https://127.0.0.1:...` or `https://localhost:...`.



### 3. **Create Required Secrets**

* Create, Edit and run the provided `create_secrets.sh` script below.
* This script creates Kubernetes secrets with default values (update for your stack as needed).
* Do not commit and push the script or any secrets!

**`create_secrets.sh`:**

```bash
#!/usr/bin/env bash
set -e

NAMESPACE="skillforge-dev"

echo "Creating demo secrets in namespace: $NAMESPACE"

kubectl create secret generic mongo-secret \
  --from-literal=MONGO_INITDB_ROOT_USERNAME=devuser \
  --from-literal=MONGO_INITDB_ROOT_PASSWORD=devpass \
  -n $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic api-secret \
  --from-literal=API_KEY=devkey123 \
  -n $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic genai-secret \
  --from-literal=GENAI_TOKEN=devtoken \
  -n $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

echo "All secrets created!"
```

**How to use:**

1. Copy the script to `create_secrets.sh` in the `helm/` folder.
2. Make it executable:

   ```bash
   chmod +x create_secrets.sh
   ```
3. Run it:

   ```bash
   ./create_secrets.sh
   ```

### 4. **Deploy All Microservices**

```bash
cd helm
helmfile deps
helmfile apply --environment dev
```

> ⚠️ Make sure `helmfile.yaml` and charts are configured to use the `skillforge-dev` namespace (or your chosen dev namespace).


### 5. **Apply Ingress**

```bash
kubectl apply -f ingress.yaml -n skillforge-dev
```


### 6. **Access the Platform**

#### For local clusters (e.g., Minikube, Docker Desktop):

* Port-forward ingress:

  ```bash
  kubectl port-forward svc/ingress-nginx-controller -n ingress-nginx 8080:80
  ```
* Open your browser:

  * [http://localhost:8080/](http://localhost:8080/) → Frontend
  * [http://localhost:8080/api](http://localhost:8080/api) → Gateway API
  * (Add more routes as needed for metabase, grafana, etc.)


## 💬 Troubleshooting

* Check all pods:

  ```bash
  kubectl get pods -n skillforge-dev
  ```
* View pod logs:

  ```bash
  kubectl logs <pod-name> -n skillforge-dev
  ```
* Re-run a failed Job (example):

  ```bash
  kubectl delete job <job-name> -n skillforge-dev
  helmfile apply --environment dev
  ```
* Delete and redeploy a release:

  ```bash
  helm uninstall <release-name> -n skillforge-dev
  helmfile apply --environment dev
  ```


## 🔑 Notes for Developers

* **Never commit secrets** or plaintext credentials to your repository.
* For production deployments, use managed secret tools (Sealed Secrets, SOPS, AWS Secrets Manager, etc.).
* You can adapt the namespace and secrets for your own stack or dev environment.


## 📚 References

* [Helmfile Docs](https://github.com/helmfile/helmfile)
* [Helm Docs](https://helm.sh/docs/)
* [Kubernetes Docs](https://kubernetes.io/docs/home/)
* [kubectl Docs](https://kubernetes.io/docs/reference/kubectl/)
* [Docker Docs](https://docs.docker.com/)