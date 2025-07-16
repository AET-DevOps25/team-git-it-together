<table>
  <tr>
    <td>
      <a href="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/docker-build-push.yml">
        <img src="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/docker-build-push.yml/badge.svg" alt="Build and Push Docker Images"/>
      </a>
    </td>
    <td>
      <a href="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/provision_configure_deploy.yml">
        <img src="https://github.com/AET-DevOps25/team-git-it-together/actions/workflows/provision_configure_deploy.yml/badge.svg" alt="Provision, Configure and Deploy to AWS"/>
      </a>
    </td>
  </tr>
  <tr>
    <td align="center"><b>Build & Push Docker Images</b></td>
    <td align="center"><b>Provision, Configure & Deploy to AWS</b></td>
  </tr>
</table>


# SkillForge.ai

**Team**: git-it-together

**Authors:** Achraf Labidi · Mahdi Bayouli 

**Tutor:** Sindi Buklaji


## 📚 Table of Contents  
1. [📅 Project Timeline](#-project-timeline)
2. [📝 Problem Statement](#-problem-statement)
3. [🧩 System Overview and Architecture](#-system-overview-and-architecture)
4. [📋 Requirements](#-requirements)  
5. [🔧 Features](#-features)  
6. [🛠️ Tech Stack](#-tech-stack)  
8. [📦 Setup Instructions](#-setup-instructions)
9. [📌 Future Improvements](#-future-improvements)  
10. [👥 Team Roles](#-team-roles)  
11. [📄 License](#-license)  
 
---

## 📅 Project Timeline

See the [weekly progress](./docs/weekly-progress.md) document for a detailed timeline of the project.

## 📝 Problem Statement

See the [problem statement](./docs/problem-statement.md) document for a detailed description of the problem statement.

## 🧩 System Overview and Architecture

> TODO: _Add link to the system overview and architecture diagram._


## 📋 Requirements

Before you get started, make sure you have the following installed on your development machine:

* **[Docker Desktop](https://www.docker.com/products/docker-desktop) (with Docker Compose)**
  *For running and orchestrating all local containers*
* **[Node.js](https://nodejs.org/) (v18+ recommended) & [npm](https://www.npmjs.com/) or [Yarn](https://yarnpkg.com/)**
  *For building and running the frontend locally (optional, but recommended for development and hot-reload)*
* **[Java JDK 21+](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html)**
  *For building and running the backend services*
* **[MongoDB](https://www.mongodb.com/try/download/community)**
  *For local development and testing (if you want to run MongoDB locally)*
* **[Weaviate](https://weaviate.io/developers/weaviate/install)**
  *For local development and testing (if you want to run Weaviate locally)*
* **[Python](https://www.python.org/downloads/) (v3.10+ recommended)**
  *For running the GenAI service locally (if you want to use GenAI features)*
* **[Git](https://git-scm.com/)**
  *For cloning the repository and managing version control*
* **[Terraform](https://www.terraform.io/downloads.html)**
  *For provisioning cloud infrastructure (if you plan to use Infrastructure as Code)*
* **[Ansible](https://docs.ansible.com/ansible/latest/installation_guide/intro_installation.html)**
  *For automating configuration and server setup (optional for local development)*
* **[kubectl](https://kubernetes.io/docs/tasks/tools/) & [Helm](https://helm.sh/docs/intro/install/)**
  *For managing Kubernetes deployments (optional, required only if deploying to a k8s cluster)*
* **[Helm](https://helm.sh/docs/intro/install/)**
  *For managing Kubernetes deployments (optional, required only if deploying to a k8s cluster)*
* **[Helmfile](https://github.com/roboll/helmfile)**
  *For managing Helm charts and releases (optional, required only if deploying to a k8s cluster)*
* **[Prometheus](https://prometheus.io/docs/prometheus/latest/installation/) & [Grafana](https://grafana.com/docs/grafana/latest/installation/)**
  *For monitoring and observability (optional, but recommended for production deployments)*
* **[OpenAI API Key](https://platform.openai.com/signup)**
  *For using GenAI features (if you want to use OpenAI's models locally)*
* **[AWS CLI](https://aws.amazon.com/cli/)**
  *For managing AWS resources (if you plan to deploy to AWS)*
* **[LM Studio](https://lmstudio.ai/)**
  *For running local language models (optional, if you want to run models locally instead of using OpenAI)* 

> **Note:**
> Most local development tasks require only Docker and Git. Other tools are needed for infrastructure automation, cloud, or advanced scenarios.

## 🔧 Features

> TODO: _List of features and functionalities that the app provides._ 


## 🛠️ Tech Stack

* **🎨 Frontend:** React, Vite, Tailwind CSS, TypeScript
* **🔗 Backend:** Java, Spring Boot
* **💾 Database:** MongoDB, Weaviate
* **🤖 GenAI:** LangChain, OpenAI, Python
* **⚙️ Infrastructure:** Docker, Docker Compose, Terraform, Ansible, Kubernetes (k8s), Helm, Helmfile
* **📈 Monitoring:** Prometheus, Grafana
* **🗂️ Version Control:** Git, GitHub
* **☁️ Cloud Provider:** AWS (Amazon Web Services), AET Cluster on Rancher


## 📦 Setup Instructions for Local Development

### **1. Prepare Your Environment**

* **Install [Docker Desktop](https://www.docker.com/products/docker-desktop/)** (includes Docker Compose).
* For Linux users, [follow these steps](https://docs.docker.com/engine/install/).

### **2. Clone the repository:**

  ```bash
  git clone https://github.com/AET-DevOps25/team-git-it-together.git
  cd team-git-it-together
  ```

### **3. Create and Configure Your `.env` File**

* **Copy the example environment file to `.env` in the root directory:**

  * **On macOS/Linux:**

    using the provided script:

    ```bash
    ./copy-env.sh .env.dev.example .env
    ```
  * **On Windows (PowerShell):**

    using the provided script:

    ```powershell
    .\copy-env.ps1 .env.dev.example .env
    ```

* **Fill in all required secrets and configurations in your new `.env` file.**

  * A minimal example:

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
  * List of Default Ports:

    | Service | Port |
    | ------- | ---- |
    | mongo | 27017 |
    | weaviate-db | 8080 |
    | server-gateway | 8081 |
    | user-service | 8082 |
    | course-service | 8083 |
    | genai | 8888 |

### **3. Build and Start the Application**

* **In the root project directory, run:**

  ```bash
  docker compose up --build
  ```

  * This will build and start all services defined in `docker-compose.yml`.

* **To run in detached mode (in the background):**

  ```bash
  docker compose up --build -d
  ```

### **4. (Optional) Seed the Database**

* **Seed the database with some data:**

  ```bash
  cd seed
  python seed_all.py
  ```
  This will will create a user with the following credentials:
  ```
  username: max123
  password: password
  ```
  and will seed the database with some courses.


### **5. Access the Application**

* **Access the application in your browser:**

  ```bash
  http://localhost:3000
  ```
  or

  ```bash
  http://client.localhost
  ```
  __Note:__ The client is accessible at `http://client.localhost` because of the `client.localhost` entry in the `/etc/hosts` file that you can add manually.
  To add it, you can run the following command:
  ```bash
  echo "127.0.0.1 client.localhost" | sudo tee -a /etc/hosts
  ```

* **Access the API using the API:**

  ```bash
  http://localhost:8081/api/v1/health
  ```
  or

  ```bash
  http://server.localhost
  ```
  __Note:__ The server is accessible at `http://server.localhost` because of the `server.localhost` entry in the `/etc/hosts` file that you can add manually.
  To add it, you can run the following command:
  ```bash
  echo "127.0.0.1 server.localhost" | sudo tee -a /etc/hosts
  ```
  
* **Access the GenAI service using the API:**

  ```bash
  http://localhost:8888/api/v1/health
  ```
  or
  ```bash
  http://genai.localhost
  ```
  __Note:__ The GenAI service is accessible at `http://genai.localhost` because of the `genai.localhost` entry in the `/etc/hosts` file that you can add manually.
  To add it, you can run the following command:
  ```bash
  echo "127.0.0.1 genai.localhost" | sudo tee -a /etc/hosts
  ```

> You can save the `client.localhost`, `server.localhost`, and `genai.localhost` entries in the `/etc/hosts` file to access the application, API, and GenAI service from your browser by doing the following:
> ```bash
> echo "127.0.0.1 client.localhost server.localhost genai.localhost" | sudo tee -a /etc/hosts
> ```

### **6. Login and Explore the Application**

* **Login with the following credentials:**

  ```
  username: max123
  password: password
  ```
  * **Explore the application:**
    * **Dashboard**
    * **Courses**
    * **Profile**
    * **Settings**
    * **Achievements**
    * **AI Center**
    * **AI Chat**
    * **Logout**

### **7. Managing the Application**

* **To stop all running containers:**

  ```bash
  docker compose down
  ```

* **To stop and remove all containers, networks, and named volumes created by `up`, including orphans:**

  ```bash
  docker compose down --remove-orphans --volumes
  ```
  **Note:** This will perform a hard reset, removing all data in volumes. Perform this only if you want to reset your local environment.

### **8. Troubleshooting & Tips**

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


## 📌 Future Improvements

> TODO: _List of potential future improvements or features that could be added to the app._



## 👥 Team Roles

* **Mahdi Bayouli** – \[Role: TBD]
* **Achraf Labidi** – \[Role: TBD]


## 📄 License

MIT License – see [LICENSE](./LICENSE) for details.

