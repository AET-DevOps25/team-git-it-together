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

```

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

````

---

## 🚦 Prerequisites

- [Terraform](https://www.terraform.io/downloads)
- [Ansible](https://docs.ansible.com/ansible/latest/installation_guide/intro_installation.html)
- AWS account and credentials (for Terraform)
- SSH private key for your EC2 instance

---

## 🏗️ Deploying Infrastructure with Terraform

1. **Configure Variables:**

   - Copy `terraform.tfvars.example` to `terraform.tfvars` and fill in required values, e.g., `key_name`, `eip_allocation_id`.

2. **Initialize Terraform:**

   ```bash
   terraform -chdir=terraform init
````

3. **Preview Infrastructure Changes:**

   ```bash
   terraform -chdir=terraform plan
   ```

4. **Apply Infrastructure Changes (DO NOT RUN ON PROD)**

   ```bash
   terraform -chdir=terraform apply
   ```

---

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

## 🧑‍💻 Automation / CI/CD

* In CI pipelines, the `terraform.tfvars` file will be dynamically generated with environment-specific values.
* Ansible playbooks can be triggered post-Terraform deployment to configure the infrastructure.
* The Ansible `inventory.ini` will also be dynamically generated based on the Terraform output and environment variables on GitHub.

---

## 📚 References

* [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
* [Ansible Docs](https://docs.ansible.com/ansible/latest/index.html)