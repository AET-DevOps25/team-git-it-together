# ✅ Project Requirements Checklist

> This file serves as a checklist for the project requirements. 
It is for the team to track progress and ensure all aspects of the project are covered.
It is not a formal document and should not be considered as such.

## 🧩 Technical Requirements - Application Stack

- [ ] **Server:** Spring Boot (Java)  
  - [ ] Exposes REST APIs  
  - [ ] Consists of at least 3 microservices  
  - [ ] Follows a modular architecture  

- [ ] **Client:** React / Angular / Vue.js  
  - [ ] Usable, responsive UI  
  - [ ] Communicates with backend over REST  

- [ ] **Database:** MySQL / PostgreSQL or similar  
  - [ ] Persistent storage  
  - [ ] Schema is documented  
  - [ ] Runs via Docker  


## 🧠 GenAI Integration

- [ ] Written in Python  
- [ ] Uses LangChain framework  
- [ ] Deployed as a modular microservice (containerized and networked with the server)  
- [ ] Fulfills a real user-facing use case (e.g., summarization, generation, Q&A)  
- [ ] Supports cloud-based models (OpenAI API)  
- [ ] Supports local models (e.g., GPT4All, LLaMA)
- [ ] Integrates a vector database (e.g., Weaviate) for Retrieval-Augmented Generation (RAG)  
- [ ] *(Bonus)* Full RAG architecture implemented using a vector store like Weaviate  


## 🐳 Containerization & Local Setup

- [ ] Each component has its own Dockerfile (min. 5 containers: **server**, **client**, **GenAI**, **DB**, **vector DB**)  
- [ ] `docker-compose.yml` included to run the system locally  
- [ ] System can be run end-to-end in 3 or fewer commands
- [ ] Default configuration works without manual ENV setup


## ☸️ Kubernetes Deployment

- [ ] Deployable to a Kubernetes cluster  
- [ ] Uses Helm charts or raw Kubernetes manifests  
- [ ] Supports local environments (e.g., Minikube, Kind)  
- [ ] Supports a cloud deployment option (TBD)  
- [ ] *(Bonus)* Can run LLMs in-cluster (e.g., with GPU support or via external API)


## 🔁 CI/CD Pipeline

- [ ] Uses GitHub Actions for CI/CD  
- [ ] CI builds and tests all services  
- [ ] CI performs static analysis/linting  
- [ ] CD automatically deploys to Kubernetes (staging) on merge to `main`  
- [ ] Uses secrets and environment-specific configuration  
- [ ] Deployment is reproducible and maintainable  


## 📊 Monitoring & Observability

- [ ] Prometheus configured to collect metrics  
- [ ] Tracks: request count, latency, error rate  
- [ ] Grafana dashboards created to visualize server + GenAI performance  
- [ ] Dashboards exported as files and included in repo  
- [ ] At least one meaningful alert rule is configured (e.g., service down or slow response)

## 🧪 Testing & Process

- [ ] Unit tests for critical backend and GenAI logic  
- [ ] Client-side tests for core user interactions  
- [ ] All tests run automatically in the CI pipeline  
- [ ] UML-style subsystem decomposition diagram included  
- [ ] Top-level architecture diagram included  
- [ ] OpenAPI/Swagger documentation provided  
- [ ] Swagger UI (or equivalent) is accessible


## 📦 Deliverables

- [ ] Complete source code (client, server, GenAI service)  
- [ ] Dockerfiles and `docker-compose.yml` for local development  
- [ ] Helm charts or Kubernetes YAML manifests with instructions  
- [ ] Prometheus + Grafana setup with dashboards and alert rules  
- [ ] Testing suite with clear instructions for running locally and in CI  
- [ ] Documentation (README or Wiki): setup, architecture, API, CI/CD, monitoring, responsibilities  
- [ ] Weekly progress reports (Markdown table format)  
- [ ] Final presentation (10–15 minutes) with each member presenting their part
