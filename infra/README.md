# 🚀 git-it-together Infrastructure Automation

Provision and configure cloud infrastructure for the git-it-together teams's platform using **Terraform** (for AWS provisioning) and **Ansible** (for configuration management).



## 🧰 What’s Inside?

- **Terraform:**  
  - Provisions AWS resources (VPC, subnet, EC2 instance, Elastic IP, security group, etc.)
- **Ansible:**  
  - Installs and configures Docker & Docker Compose on the provisioned EC2 instance
  - Adds the `ubuntu` user to the `docker` group for passwordless Docker commands



## 📂 Project Structure

   ```markdown
   infra/
   ├── terraform/
   │   ├── main.tf
   │   ├── variables.tf
   │   ├── outputs.tf
   │   ├── terraform.tfvars.example
   │   ├── README.md
   │   └── .gitignore
   └── ansible/
   │   ├── playbook.yml
   │   ├── inventory.example.ini
   │   ├── README.md
   │   └── .gitignore
   └── README.md
   ```



## 🚦 Prerequisites

- [Terraform](https://www.terraform.io/downloads)
- [Ansible](https://docs.ansible.com/ansible/latest/installation_guide/intro_installation.html)
- [AWS CLI](https://aws.amazon.com/cli/) configured with your AWS account
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
- [Helm](https://helm.sh/docs/intro/install/)
- [Helmfile](https://github.com/helmfile/helmfile#installation) (recommended for multi-chart orchestration)
- kubectl configured with your Kubernetes cluster
- AWS account and credentials (for Terraform)
- SSH private key for your EC2 instance


## 🏗️ Deploying Infrastructure with Terraform

1. **Configure Variables:**

   - Copy `terraform.tfvars.example` to `terraform.tfvars` and fill in required values, e.g., `key_name`, `eip_allocation_id`.

2. **Initialize Terraform:**

   ```bash
   terraform -chdir=terraform init
   ```

3. **Preview Infrastructure Changes:**

   ```bash
   terraform -chdir=terraform plan
   ```

4. **Apply Infrastructure Changes (DO NOT RUN ON PROD)**

   ```bash
   terraform -chdir=terraform apply
   ```



## 🛠️ Server Configuration with Ansible

1. **Prepare your Ansible inventory:**

   * Copy `inventory.example.ini` to `inventory.ini`.
   * Add your EC2 public IP (from Terraform output):

     ```
     [git_it_together]
     <EC2_PUBLIC_IP> ansible_user=ubuntu ansible_ssh_private_key_file=./labuser.pem
     ```
   * Make sure your SSH key (`labuser.pem`) is present and has `chmod 600` permissions.

2. **Dry Run the Playbook:**

   ```bash
   ansible-playbook -i inventory.ini docker-setup.yml --check
   ```

3. **(Optional) Run the Playbook (DO NOT RUN ON PROD):**

   ```bash
   ansible-playbook -i inventory.ini docker-setup.yml
   ```



## 🏗️ Kubernetes & Helm Deployment

### 1. **Kubeconfig Setup**

* **Download the Rancher `student` KubeConfig.**
* Place it at `~/.kube/config` (backup your previous file if necessary).
* Test connection:

  ```bash
  kubectl config current-context
  # Should return: student
  ```



### 2. **Ensure Required CLI Tools are Installed**

* [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
* [Helm](https://helm.sh/docs/intro/install/)
* [Helmfile](https://github.com/helmfile/helmfile#installation) (recommended for multi-chart orchestration)



### 3. **Namespace Creation**

> **Important:** Due to RBAC policies, create the namespace **via the Rancher UI**, not `kubectl`.

* In Rancher:
  Go to **"Namespaces" → "Create Namespace"**
  Name: `devops25-git-it-together-prod`



### 4. **(Optional) Namespace Metadata**

If you have labels/annotations to add (e.g., `k8s/namespace.yaml`), you can apply them **once**:

```bash
kubectl apply -f k8s/namespace.yaml
```

> Only do this if advised—usually, creating via Rancher UI is sufficient.



### 5. **Helm Chart Structure**

Your charts are organized as an umbrella chart with service subcharts:

```
infra/helm/
└── skill-forge-ai/
    ├── client/
    ├── gateway/
    ├── genai/
    ├── user/
    ├── helmfile.yaml
    └── ...
```

### 6. Deploy with Helmfile 🚀

> **Note:** Each service chart (`client`, `gateway`, `genai`, `user`) is defined as a separate Helm chart, and there is **no umbrella chart** at the root.  
> You must use [Helmfile](https://github.com/helmfile/helmfile) to orchestrate deployment of all service charts together.

From the `infra/helm/skill-forge-ai` directory, run:

```bash
helmfile deps      # Fetches all chart dependencies (if any)
helmfile apply     # Installs/updates all defined charts
## or
helmfile sync  # (runs without interactive diff)
```

> **Do not** run `helm install ./skill-forge-ai ...` as this will not deploy all services.
> Use `helmfile` for multi-chart orchestration.

### **💡 Useful Commands**

#### **Helm**

* **List all Helm releases in a namespace:**

  ```bash
  helm list -n devops25-git-it-together-prod
  ```

* **Get detailed information about a specific release:**

  ```bash
  helm status <release-name> -n devops25-git-it-together-prod
  ```

* **Upgrade (update) a release:**

  ```bash
  helm upgrade <release-name> <chart-path> -n devops25-git-it-together-prod
  ```

* **Uninstall (delete) a release:**

  ```bash
  helm uninstall <release-name> -n devops25-git-it-together-prod
  ```

#### **Kubernetes (kubectl)**

* **List all resources in a namespace:**

  ```bash
  kubectl get all -n devops25-git-it-together-prod
  ```

* **Describe a resource (e.g., pod, service, deployment):**

  ```bash
  kubectl describe <resource-type> <resource-name> -n devops25-git-it-together-prod
  # Example:
  kubectl describe pod <pod-name> -n devops25-git-it-together-prod
  ```

* **View logs for a pod:**

  ```bash
  kubectl logs <pod-name> -n devops25-git-it-together-prod
  ```

* **Port-forward a service (for local testing):**

  ```bash
  kubectl port-forward svc/<service-name> 1234:80 -n devops25-git-it-together-prod
  ```



#### **Helmfile**

* **Install or sync all defined releases (charts):**

  ```bash
  helmfile apply
  # Or, for non-interactive apply:
  helmfile sync
  ```

* **Show what changes will be made (diff):**

  ```bash
  helmfile diff
  ```

* **Uninstall all releases defined in helmfile.yaml:**

  ```bash
  helmfile destroy
  ```



#### **General Tips**

* Replace `<release-name>`, `<resource-type>`, `<resource-name>`, and `<service-name>` with your actual names.
* Always double-check you’re operating in the correct namespace!



## 🚀 Deploying to Kubernetes with Helmfile (Same as in Step 6: Deploy with Helmfile):

1. **Prepare your kubeconfig from Rancher and verify the context.**
2. **Ensure the namespace is created via the Rancher UI:**
   Name: `devops25-git-it-together-prod`
3. **Install chart dependencies:**

   ```bash
   cd infra/helm/skill-forge-ai
   helmfile deps
   ```
4. **Apply all Helm charts:**

   ```bash
   helmfile apply
   ```
5. **Verify deployment:**

   ```bash
   kubectl get all -n devops25-git-it-together-prod
   ```
6. **Access services as required (see above).**


## 🧑‍💻 Automation / CI/CD

* In CI pipelines, the `terraform.tfvars` file will be dynamically generated with environment-specific values.
* Ansible playbooks can be triggered post-Terraform deployment to configure the infrastructure.
* The Ansible `inventory.ini` will also be dynamically generated based on the Terraform output and environment variables on GitHub.



## 📚 References

* [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
* [Ansible Docs](https://docs.ansible.com/ansible/latest/index.html)
* [Kubernetes Docs](https://kubernetes.io/docs/home/)
* [Helm Docs](https://helm.sh/docs/)
* [Helmfile GitHub](https://github.com/helmfile/helmfile)
* [Kubernetes CLI (kubectl) Docs](https://kubernetes.io/docs/reference/kubectl/)
* [Docker Docs](https://docs.docker.com/)
* [Docker Compose Docs](https://docs.docker.com/compose/)
* [Helmfile Docs](https://github.com/helmfile/helmfile)
* [Rancher Docs](https://ranchermanager.docs.rancher.com/)

## 🧑‍💻 Automation / CI/CD

* **Terraform:**

  * In CI pipelines, the `terraform.tfvars` file is dynamically generated using environment-specific variables and secrets.
  * Runs infrastructure provisioning jobs (VPC, subnets, EC2, security groups, etc.) in a repeatable and reviewable manner.
* **Ansible:**

  * Playbooks are triggered automatically after successful Terraform apply, to configure the provisioned EC2 instance(s).
  * The `inventory.ini` file is dynamically generated from the Terraform output using GitHub Actions or another pipeline tool, ensuring correct host IPs and SSH details.
* **Docker & Docker Compose:**

  * Used for local development and can be orchestrated in CI for building and testing application containers before Kubernetes deployment.
* **Kubernetes & Helmfile:**

  * CI pipelines use Helmfile to synchronize chart dependencies and deploy application microservices to the target Kubernetes cluster.
  * Environments (dev, staging, prod) can be parameterized using Helmfile’s `environments:` feature and values files.
* **Secrets & Configs:**

  * Sensitive configuration is injected via environment variables or secret management (e.g., Kubernetes Secrets, AWS SSM, or GitHub Actions secrets).
* **Automated Deployment Example:**

  1. Terraform applies infrastructure.
  2. Ansible configures servers and installs dependencies.
  3. Helmfile deploys and synchronizes all service charts to Kubernetes.
  4. CI checks and tests validate each stage before proceeding to the next.

## 📚 References

* [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
* [Terraform CLI Docs](https://developer.hashicorp.com/terraform/cli)
* [Ansible Docs](https://docs.ansible.com/ansible/latest/index.html)
* [Kubernetes Docs](https://kubernetes.io/docs/home/)
* [Kubernetes CLI (kubectl) Docs](https://kubernetes.io/docs/reference/kubectl/)
* [Helm Docs](https://helm.sh/docs/)
* [Helmfile Docs](https://github.com/helmfile/helmfile)
* [Docker Docs](https://docs.docker.com/)
* [Docker Compose Docs](https://docs.docker.com/compose/)
* [Rancher Docs](https://ranchermanager.docs.rancher.com/)
* [CI/CD Patterns with GitHub Actions](https://docs.github.com/en/actions)
* [Best Practices for GitOps and Kubernetes](https://www.weave.works/technologies/gitops/)

