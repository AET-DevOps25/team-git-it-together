# SkillForge.ai

**Team**: git-it-together

**Authors:** Achraf Labidi (@GravityDarkLab) x Mahdi Bayouli  (@mahdibayouli)

**Tutor:** Sindi Buklaji

**Description:** SkillForge is an innovative AI-powered learning platform that revolutionizes how people acquire new skills and knowledge. Our mission is to make quality education accessible, personalized, and engaging for everyone, regardless of their location or background.


## 📚 Table of Contents  
1. [📊 Status](#-status)
2. [🌐 Live Application Links](#-live-application-links)
3. [📅 Internal Project Timeline](#-internal-project-timeline)
4. [📝 Problem Statement](#-problem-statement)
5. [🧩 System Overview and Architecture](#-system-overview-and-architecture)
6. [📋 Requirements](#-requirements)  
7. [🔧 Features](#-features)  
8. [🛠️ Tech Stack](#-tech-stack)  
9. [📦 Setup Instructions](#-setup-instructions)
10. [👥 Team Roles](#-team-roles)  
11. [📄 License](#-license)  
 

## 📊 Status

<div align="center">

<table>
  <tr>
    <td colspan="3" align="center"><b>🔨 Build & Test</b></td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/build-and-test-client.yml">
        <img src="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/build-and-test-client.yml/badge.svg" alt="Build and Test Client"/>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/build-and-test-server.yml">
        <img src="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/build-and-test-server.yml/badge.svg" alt="Build and Test Server"/>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/build-and-test-genai.yml">
        <img src="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/build-and-test-genai.yml/badge.svg" alt="Build and Test GenAI"/>
      </a>
    </td>
  </tr>
  <tr>
    <td align="center"><b>Client</b></td>
    <td align="center"><b>Server</b></td>
    <td align="center"><b>GenAI</b></td>
  </tr>
  <tr>
    <td colspan="3" align="center"><b>🐳 Docker</b></td>
  </tr>
  <tr>
    <td colspan="3" align="center">
      <a href="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/docker-build-push.yml">
        <img src="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/docker-build-push.yml/badge.svg" alt="Build and Push Docker Images"/>
      </a>
    </td>
  </tr>
  <tr>
    <td colspan="3" align="center"><b>☸️ Kubernetes Deployment</b></td>
  </tr>
  <tr>
    <td colspan="3" align="center">
      <a href="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/build-push-deploy.yml">
        <img src="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/build-push-deploy.yml/badge.svg" alt="Build, Push and Deploy to Kubernetes"/>
      </a>
    </td>
  </tr>
  <tr>
    <td colspan="3" align="center"><b>☁️ AWS Deployment</b></td>
  </tr>
  <tr>
    <td colspan="3" align="center">
      <a href="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/provision_configure_deploy.yml">
        <img src="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/provision_configure_deploy.yml/badge.svg" alt="Provision, Configure and Deploy to AWS"/>
      </a>
    </td>
  </tr>
</table>

</div>

## 🌐 Live Application Links

| Service | Description | URL |
|---------|-------------|-----|
| 🎯 **Main Application** | SkillForge.ai Frontend | [https://skillforge.student.k8s.aet.cit.tum.de](https://skillforge.student.k8s.aet.cit.tum.de) |
| 🔌 **API Gateway** | Backend API Services | [https://api.skillforge.student.k8s.aet.cit.tum.de/api/v1](https://api.skillforge.student.k8s.aet.cit.tum.de/api/v1) |
| 🤖 **GenAI** | GenAI Service | [https://api.genai.skillforge.student.k8s.aet.cit.tum.de/api/v1](https://api.genai.skillforge.student.k8s.aet.cit.tum.de/api/v1) |
| 📈 **Prometheus** | Metrics & Monitoring | [https://prometheus.skillforge.student.k8s.aet.cit.tum.de](https://prometheus.skillforge.student.k8s.aet.cit.tum.de) |
| 📊 **Grafana** | Dashboards & Analytics | [https://grafana.skillforge.student.k8s.aet.cit.tum.de](https://grafana.skillforge.student.k8s.aet.cit.tum.de) |
| 🚨 **Alert Manager** | Alerts & Notifications | [https://alertmanager.skillforge.student.k8s.aet.cit.tum.de](https://alertmanager.skillforge.student.k8s.aet.cit.tum.de) |
| 📧 **MailHog** | Email Testing Tool | [https://mailhog.skillforge.student.k8s.aet.cit.tum.de](https://mailhog.skillforge.student.k8s.aet.cit.tum.de) |

## 📅 Internal Project Timeline

See the [weekly progress](https://confluence.aet.cit.tum.de/spaces/DO25WR/pages/258581490/git-it-together) document for a detailed timeline of the project.

## 📝 Problem Statement

See the [problem statement](./docs/problem-statement.md) document for a detailed description of the problem statement.

## 🧩 System Overview and Architecture

See the [system overview and architecture](./docs/system-overview-and-architecture.md) document for a detailed description of the system overview and architecture.

For the Server, See the [README](./server/README.md) file for a detailed description of the server.
For the Client, See the [README](./client/README.md) file for a detailed description of the client.
For the GenAI, See the [README](./genai/README.md) file for a detailed description of the GenAI.

## 📋 Requirements

### 🚀 **Essential Requirements (Local Development)**
* **[Docker Desktop](https://www.docker.com/products/docker-desktop) (with Docker Compose)**
  *Required for running and orchestrating all local containers*
* **[Git](https://git-scm.com/)**
  *Required for cloning the repository and managing version control*

### 💻 **Development Tools (Recommended)**
* **[Node.js](https://nodejs.org/) (v18+ recommended) & [npm](https://www.npmjs.com/) or [Yarn](https://yarnpkg.com/)**
  *For building and running the frontend locally with hot-reload*
* **[Java JDK 21+](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html)**
  *For building and running the backend services locally*
* **[Python](https://www.python.org/downloads/) (v3.10+ recommended)**
  *For running the GenAI service locally and database seeding*

### 🗄️ **Local Services (Optional)**
* **[MongoDB](https://www.mongodb.com/try/download/community)**
  *For local development and testing (Docker version is used by default)*
* **[Weaviate](https://weaviate.io/developers/weaviate/install)**
  *For local development and testing (Docker version is used by default)*

### 🤖 **AI/ML Requirements**
* **[OpenAI API Key](https://platform.openai.com/signup)**
  *Required for using GenAI features with OpenAI models*
* **[LM Studio](https://lmstudio.ai/) (Optional)**
  *For running local language models instead of using OpenAI*

### ☁️ **Infrastructure & Deployment (Optional)**
* **[Terraform](https://www.terraform.io/downloads.html)**
  *For provisioning cloud infrastructure (Infrastructure as Code)*
* **[Ansible](https://docs.ansible.com/ansible/latest/installation_guide/intro_installation.html)**
  *For automating configuration and server setup*
* **[kubectl](https://kubernetes.io/docs/tasks/tools/)**
  *For managing Kubernetes deployments*
* **[Helm](https://helm.sh/docs/intro/install/)**
  *For managing Kubernetes deployments and Helm charts*
* **[Helmfile](https://github.com/roboll/helmfile)**
  *For managing Helm charts and releases*
* **[AWS CLI](https://aws.amazon.com/cli/)**
  *For managing AWS resources*

### 📊 **Monitoring & Observability (Optional)**
* **[Prometheus](https://prometheus.io/docs/prometheus/latest/installation/)**
  *For metrics collection and monitoring*
* **[Grafana](https://grafana.com/docs/grafana/latest/installation/)**
  *For dashboards and analytics*

> **💡 Note:** Most local development tasks require only **Docker** and **Git**. Other tools are needed for infrastructure automation, cloud deployment, or advanced development scenarios.

## 🔧 Features


Built with cutting-edge technologies and modern development practices, SkillForge combines the power of artificial intelligence with comprehensive learning management to create a truly personalized educational experience. Our platform adapts to each learner's pace, preferences, and goals, ensuring maximum engagement and retention.

### 🔥 **Current Features**

* ✨ **AI-Curated Course Generation**
  Create and explore dynamic courses powered by artificial intelligence.

* 💬 **Interactive AI Chat Assistant**
  Get instant help and learning guidance from an intelligent AI tutor.

* 🧭 **Personalized Learning Paths**
  Customized journeys tailored to your skills, pace, and goals.

* 🏆 **Achievement & Badge System**
  Unlock milestones and collect badges as you learn.

* 📚 **Course Bookmarking**
  Save and organize your favorite courses for quick access.

* ⚡️ **Real-time Progress Updates**
  Track your learning progress instantly, every step of the way.

---

### 🚀 **Planned Features**

* 🌐 **Global Learning Community** *(Coming Soon)*
  Connect, collaborate, and grow with learners worldwide.

* 🈺 **Multi-language Support** *(Coming Soon)*
  Learn in your preferred language.

* 📊 **Advanced Analytics Dashboard** *(Coming Soon)*
  Dive deep into your learning stats and trends.

* 💻 **Interactive Code Playground** *(Coming Soon)*
  Practice coding live within the platform.

* 🎓 **Certification Programs** *(Coming Soon)*
  Earn certificates to showcase your achievements.


## 🛠️ Tech Stack

* **🎨 Frontend:** React, Vite, Tailwind CSS, TypeScript
* **🔗 Backend:** Java, Spring Boot
* **💾 Database:** MongoDB, Weaviate
* **🤖 GenAI:** LangChain, OpenAI, Python
* **⚙️ Infrastructure:** Docker, Docker Compose, Terraform, Ansible, Kubernetes (k8s), Helm, Helmfile
* **📈 Monitoring:** Prometheus, Grafana
* **🗂️ Version Control:** Git, GitHub
* **☁️ Cloud Provider:** AWS (Amazon Web Services), AET Cluster on Rancher


## 📦 Quick Start: Local Development Setup

### 1. Prerequisites
- **Docker Desktop** (includes Docker Compose) — [Download here](https://www.docker.com/products/docker-desktop)
- **Git**
- (Linux only) [Follow these steps](https://docs.docker.com/engine/install/) to install Docker Engine

### 2. Clone the Repository
```bash
git clone https://github.com/AET-DevOps25/team-git-it-together.git
cd team-git-it-together
```

### 3. Configure Environment Variables
- Copy the example environment file to `.env` in the root directory:
  - **macOS/Linux:**
    ```bash
    ./copy-env.sh .env.dev.example .env
    ```
  - **Windows (PowerShell):**
    ```powershell
    .\copy-env.ps1 .env.dev.example .env
    ```
- Edit `.env` and fill in all required secrets and configuration values.
- **Minimal example:**
  ```env
  MONGODB_DATABASE=skillforge_dev
  MONGODB_USERNAME=dev_user
  MONGODB_PASSWORD=dev_password
  JWT_SECRET=<secret-key>
  JWT_EXPIRATION_MS=3600000
  VITE_APP_VERSION=1.0.0
  VITE_API_VERSION=v1
  GENAI_APP_VERSION=1.0.0
  CORS_ALLOW_ORIGINS=*
  LLM_PROVIDER=openai
  OPENAI_API_BASE=https://api.openai.com/v1
  OPENAI_API_KEY=<secret-key>
  OPENAI_MODEL=gpt-4o-mini
  ```
- **Default Ports:**
  | Service         | Port  |
  |-----------------|-------|
  | mongo           | 27017 |
  | weaviate-db     | 8080  |
  | server-gateway  | 8081  |
  | user-service    | 8082  |
  | course-service  | 8083  |
  | genai           | 8888  |
  | mailhog         | 8025  |
  | prometheus      | 9090  |
  | grafana         | 3000  |
  | alertmanager    | 9093  |
  | client          | 3000  |

### 4. Build and Start All Services
```bash
docker compose up --build
```
- To run in detached/background mode:
  ```bash
  docker compose up --build -d
  ```

### 5. (Optional) Seed the Database
```bash
cd seed
python seed_all.py
```
- This will create a demo user and seed the database with sample courses.

### 6. Access the Application
- **Frontend:** [http://localhost:3000](http://localhost:3000) or [http://client.localhost](http://client.localhost)
- **API Gateway:** [http://localhost:8081/api/v1/health](http://localhost:8081/api/v1/health) or [http://server.localhost](http://server.localhost)
- **GenAI Service:** [http://localhost:8888/api/v1/health](http://localhost:8888/api/v1/health) or [http://genai.localhost](http://genai.localhost)

> **Tip:** For pretty URLs like `client.localhost`, `server.localhost`, and `genai.localhost`, add this to your `/etc/hosts` file:
> ```bash
> echo "127.0.0.1 client.localhost server.localhost genai.localhost" | sudo tee -a /etc/hosts
> ```

---

### ℹ️ Running Services Individually
To run a specific service (e.g., client, server, genai) on its own, see the README in that service's directory for detailed instructions:
- [`client/README.md`](client/README.md)
- [`server/README.md`](server/README.md)
- [`genai/README.md`](genai/README.md)

---

### **8. Managing the Application**

* **To stop all running containers:**

  ```bash
  docker compose down
  ```

* **To stop and remove all containers, networks, and named volumes created by `up`, including orphans:**

  ```bash
  docker compose down --remove-orphans --volumes
  ```
  **Note:** This will perform a hard reset, removing all data in volumes. Perform this only if you want to reset your local environment.

### **9. Troubleshooting & Tips**

* **Check logs for any service:**

  ```bash
  docker-compose logs <service-name>
  ```
  Listed services include:
  - `client`
  - `server-gateway`
  - `user-service`
  - `course-service`
  - `weaviate-db`
  - `genai`
  - `mongo`

* **See running containers:**

  ```bash
  docker ps
  ```
* **If you change `.env`, rebuild containers:**

  ```bash
  docker-compose up --build
  ```
* **To Try the production setup locally:**

  * Create a `.env.prod` file based on `.env.prod.example` and fill in the required values.
  * Run the production setup with:

    ```bash
    docker compose --env-file .env.prod -f docker-compose.local.yaml up --build -d
    ```
  * **To stop and delete all containers, networks, and volumes of the production setup:**

    ```bash
    docker compose --env-file .env.prod -f docker-compose.local.yaml down --volumes --remove-orphans
    ```


## 👥 Team Roles

### **Achraf Labidi** – Full Stack Software Developer
* **Backend Development** - Java Spring Boot services (Gateway, User Service, Course Service)
* **Frontend Development** - React/TypeScript application with Vite and Tailwind CSS
* **Deployment & Infrastructure** - Docker containerization, Kubernetes deployment, AWS deployment, CI/CD pipelines

### **Mahdi Bayouli** – Full Stack Software Developer  
* **GenAI Development** - Python-based AI services with LangChain and OpenAI integration
* **Monitoring & Observability** - Prometheus, Grafana, Alert Manager setup and configuration
* **Deployment & Infrastructure** - Kubernetes deployment and monitoring stack


## 📄 License

MIT License – see [LICENSE](./LICENSE) for details.

