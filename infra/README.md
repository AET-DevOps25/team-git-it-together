# 🚀 git-it-together Infrastructure Automation

Provision and configure cloud infrastructure for the git-it-together teams's platform using **Terraform** (for AWS provisioning) and **Ansible** (for configuration management).

---

## 🧰 What’s Inside?

- **Terraform:**  
  - Provisions AWS resources (VPC, subnet, EC2 instance, Elastic IP, security group, etc.)
- **Ansible:**  
  - Installs and configures Docker & Docker Compose on the provisioned EC2 instance
  - Adds the `ubuntu` user to the `docker` group for passwordless Docker commands

---

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

---

## 🚦 Prerequisites

- [Terraform](https://www.terraform.io/downloads)
- [Ansible](https://docs.ansible.com/ansible/latest/installation_guide/intro_installation.html)
- [AWS CLI](https://aws.amazon.com/cli/) configured with your AWS account
- [KUbernetes CLI (kubectl)](https://kubernetes.io/docs/tasks/tools/) (if you plan to use Kubernetes)
- [helm](https://helm.sh/docs/intro/install/) (if you plan to use Helm for Kubernetes deployments)
- [Docker](https://docs.docker.com/get-docker/) (for local development)
- [Docker Compose](https://docs.docker.com/compose/install/) (for local development)
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

---

##  🏗️ Deploying Infrastructure with Kubernetes and Helm into Rancher

1. **Ensure you download the KubeConfig file `student` from Rancher.**
   - This file is essential for connecting to your Kubernetes cluster.
   - Save it to your local machine (e.g., `~/.kube/config`). (Ensure you create a backup of your existing kubeconfig if you have one.)
2. **Ensure you have the `kubectl` and `helm` CLI tools installed.**
   - [Install kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
   - [Install Helm](https://helm.sh/docs/intro/install/)
3. **Ensure the kubectl context is set to the Rancher cluster (student).**
   - You can check this by running:
     ```bash
     kubectl config current-context
     ```
     Which should return `student`.
4. **Ensure You manually create the `git-it-together` namespace in the kubernetes cluster.**
   - You can do this by running:
     ```bash
     kubectl create namespace devops25-git-it-together-prod
     ```
     But due to Access Control Policies, Do not run this command as the namespace will be created but won't be visible in the Rancher UI.
   - Instead, you can create the namespace via the Rancher UI by navigating to the "Namespaces" section and clicking "Create Namespace".
   > The Namespace should also be created - No need to create it again.

5. **To verify the namespace creation, you can run:**
   ```bash
   kubectl get ns devops25-git-it-together-prod
   ```
6. **To apply some metadata to the namespace, you can use k8s/namespace.yaml file.**
   - This file contains metadata such as labels and annotations that can be useful for organizing and managing resources within the namespace.
   - You can apply it by running:
     ```bash
     kubectl apply -f k8s/namespace.yaml
     ```
   > This also should not be run again as the namespace is already created and the metadata is already applied.
7. **Deploy the Helm charts:**
   - Navigate to the `infra/helm` directory.
   - Use the following command to deploy the Helm charts:
     ```bash
     helm install skill-forge-ai ./skill-forge-ai --namespace devops25-git-it-together-prod
     ```
   - This command will deploy the SkillForge.ai application into the `devops25-git-it-together-prod` namespace.
8. **Verify the deployment:**
   - You can check the status of the deployment by running:
     ```bash
     kubectl get all -n devops25-git-it-together-prod
     ```
   - This will show you all the resources created in the `devops25-git-it-together-prod` namespace, including pods, services, and deployments.
9. **Access the application:**
   - Once the deployment is verified, you can access the SkillForge.ai application using the service's external IP or domain name.
   - If you want to access cluster-internal services, you can utilize port-forwarding:
     ```bash
     kubectl port-forward svc/skill-forge-ai-<service-name> 1234:80 -n devops25-git-it-together-prod
     ```
     > Do not use ports 8080, 8081, 8082 or 8083 as they are already used by the Rancher UI and other services.
10. **Cleanup:**
   - If you need to remove the deployment, you can run:
     ```bash
     helm uninstall skill-forge-ai --namespace devops25-git-it-together-prod
     ```


## 🧑‍💻 Automation / CI/CD

* In CI pipelines, the `terraform.tfvars` file will be dynamically generated with environment-specific values.
* Ansible playbooks can be triggered post-Terraform deployment to configure the infrastructure.
* The Ansible `inventory.ini` will also be dynamically generated based on the Terraform output and environment variables on GitHub.

---

## 📚 References

* [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
* [Ansible Docs](https://docs.ansible.com/ansible/latest/index.html)