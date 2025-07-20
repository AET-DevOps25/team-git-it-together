# SkillForge GenAI Service

## Overview

The SkillForge GenAI Service is a Python/FastAPI microservice that provides AI-powered features for the SkillForge learning platform. It includes web crawling, content embedding, vector search, and AI-powered course generation capabilities.

## Features

- **Web Crawling**: Extract and clean content from websites
- **Content Embedding**: Convert text to vector embeddings for semantic search
- **Vector Search**: Query embedded content using semantic similarity
- **AI Course Generation**: Generate courses using LLMs and RAG (Retrieval-Augmented Generation)
- **Scheduled Jobs**: Automated content processing and embedding
- **Health Monitoring**: Prometheus metrics and health checks
- **API Documentation**: Swagger UI and OpenAPI specification

## Architecture

```
┌─────────────────────────────────────┐
│         GenAI Service               │
│         (Port 8888)                 │
│  • FastAPI Application              │
│  • Web Crawling & Embedding         │
│  • Vector Search & RAG              │
│  • LLM Integration                  │
│  • Scheduled Jobs                   │
└─────────────┬───────────────────────┘
              │
┌─────────────▼─────────────┐
│        Weaviate           │
│      (Port 8080)          │
│  • Vector Database        │
│  • Semantic Search        │
│  • Document Storage       │
└───────────────────────────┘
```

## Prerequisites

- **Python 3.10+** (Recommended: use [conda](https://docs.conda.io/en/latest/) or [pyenv](https://github.com/pyenv/pyenv))
- **Docker & Docker Compose**
- **OpenAI API Key** (or other LLM provider)
- **Git**

## Environment Variables

Create a `.env` file in the `genai/` directory. Example:

```env
# Weaviate Configuration (used by GenAI)
WEAVIATE_HOST=localhost
WEAVIATE_HTTP_PORT=8080
WEAVIATE_GRPC_PORT=50051

# GenAI Service Configuration
GENAI_PORT=8888
GENAI_APP_NAME=skill-forge-genai-dev
GENAI_APP_VERSION=0.0.1
IS_DEV_MODE=1
UVICORN_WORKERS=1
CORS_ALLOW_ORIGINS=*

# LLM Provider Configurations
# For LLMStudio
LLM_PROVIDER=llmstudio
OPENAI_API_BASE=http://127.0.0.1:1234/v1
OPENAI_API_KEY=whateveveryouwant
OPENAI_MODEL=qwen/qwen3-8b

# For OpenAI (uncomment to use)
# LLM_PROVIDER=openai
# OPENAI_API_BASE=https://api.openai.com/v1
# OPENAI_API_KEY=sk-proj-xxxxxx
# OPENAI_MODEL=gpt-4o-mini
```

---

# 🚀 Quick Start

## 1. Local Python (conda/pyenv) + Docker Weaviate

### Step 1: Start Weaviate (Docker)
```bash
# From the project root (where docker-compose.yml is)
docker-compose up -d skillforge-weaviate
```
- This will start Weaviate on port 8088 (external) mapped to 8080 (internal in container).
- Health: http://localhost:8088/v1/.well-known/ready

### Step 2: Set up Python Environment

#### Using Conda (recommended)
```bash
# Option A: Using environment.yml (recommended)
conda env create -f environment.yml
conda activate genai-devops25

# Option B: Manual setup
conda create -n genai python=3.10 -y
conda activate genai
pip install -r requirements.txt
```
#### Using Pyenv
```bash
pyenv install 3.10.13
pyenv local 3.10.13
# (Optional) pyenv virtualenv 3.10.13 genai && pyenv activate genai
pip install -r requirements.txt
```

### Step 3: Configure Environment
- Copy the example above to `.env` and set your keys.
- Make sure `WEAVIATE_HOST=localhost` and `WEAVIATE_HTTP_PORT=8088` (since Weaviate is exposed on 8088 by docker-compose).

### Step 4: Run GenAI Service
```bash
# Run the service (environment variables are loaded automatically by python-dotenv)
python src/main.py
```

- Service: http://localhost:8888
- Docs: http://localhost:8888/docs
- Health: http://localhost:8888/ping

---

## 2. Full Docker Compose (GenAI + Weaviate) - Recommended:

### Step 1: Copy/Edit `.env`
- Use the example above, but set `WEAVIATE_HOST=skillforge-weaviate` (the docker service name).
- Set your OpenAI or LLMStudio keys.

### Step 2: Start Both Services
```bash
# From the project root
docker-compose up -d skillforge-genai skillforge-weaviate
```
- GenAI: http://localhost:8888
- Weaviate: http://localhost:8088

### Step 3: Check Health
- GenAI: http://localhost:8888/ping
- Weaviate: http://localhost:8088/v1/.well-known/ready

---

## Troubleshooting
- If GenAI can't connect to Weaviate, check your `.env` and port mappings.
- For local Python, use `WEAVIATE_HOST=localhost` and `WEAVIATE_HTTP_PORT=8088`.
- For Docker Compose, use `WEAVIATE_HOST=skillforge-weaviate` and `WEAVIATE_HTTP_PORT=8080`.
- View logs: `docker-compose logs -f skillforge-genai skillforge-weaviate`

---

## API Endpoints

- `GET /ping` - Lightweight health check
- `GET /api/v1/health` - Deep health check with dependencies
- `GET /docs` - Swagger UI documentation
- `GET /docs/genai-openapi.yaml` - OpenAPI specification
- `POST /api/v1/crawl` - Crawl and extract content from URLs
- `POST /api/v1/embed` - Embed content into vector database
- `POST /api/v1/query` - Query embedded content
- `POST /api/v1/generate` - Generate text using LLMs
- `POST /api/v1/rag/generate-course` - Generate courses using RAG
- `GET /api/v1/scheduler/status` - Get scheduler status
- `POST /api/v1/scheduler/control` - Control scheduler
- `POST /api/v1/scheduler/run-now` - Run scheduled jobs immediately