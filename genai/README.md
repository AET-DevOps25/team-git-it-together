# 🛠 Local Development Setup Guide

This guide walks you through setting up Weaviate and the GenAI server for local development.

---
## 📦 Prerequisites

- [Docker](https://www.docker.com/get-started) installed and running
- [Python 3.10+](https://www.python.org/downloads/)
- [VS Code](https://code.visualstudio.com/) or any other code editor
- [Conda](https://docs.conda.io/en/latest/) (optional, for environment management) or [venv](https://docs.python.org/3/library/venv.html) (for Python virtual environments)
- (Optional) [Postman](https://www.postman.com/) or [Insomnia](https://insomnia.rest/) for API testing

---

## ⚙️ Environment Variables

Create a file named `.env` in the `genai/` (root) directory:

```env
WEAVIATE_HOST=localhost
WEAVIATE_HTTP_PORT=1234
WEAVIATE_GRPC_PORT=50051
```


## 🐍 Setting Up the Python Environment

### 🐧 macOS / Linux

```bash
chmod +x starter-scripts/env-setup.sh
./starter-scripts/env-setup.sh [conda|venv]
```

### 🪟 Windows (Command Prompt)

```cmd
env-setup.bat [conda|venv]
```

This will:
- Create a new Conda or venv environment named `genai-devops25`
- Install the required packages from `requirements.txt`
- Activate the environment.

If you choose to use `conda`, and the environment is not activated automatically, you can activate it manually using:

```bash
conda activate genai-devops25
```

To reset the environment, you can delete the `genai-devops25` environment using:
```bash
conda deactivate
conda env remove -n genai-devops25
```
Or
```bash
rm -rf venv
```

And then run the setup script again.



## 🚀 Starting Weaviate for Local Developement

You can start Weaviate with the provided scripts:
### 🐧 macOS / Linux

```bash
chmod +x starter-scripts/*.sh
./start-weaviate.sh
```
### 🪟 Windows (Command Prompt)

```cmd
start-weaviate.bat
```

This will:
- Pull and run the official docker-compose Weaviate image
- Create a Weaviate instance with the following settings:
  - `WEAVIATE_URL`: `http://localhost:1234`
  - `WEAVIATE_PORT`: `1234`
  - `WEAVIATE_GRPC_PORT`: `50051`



## 🚀 Running the GenAI Server

To run the GenAI server locally:

### 🐧 macOS / Linux

```bash
# ensure you are in the genai directory
cd genai
# run the app
python src/main.py
```

### 🪟 Windows

```cmd
cd genai
python src\main.py
```

### Docker

You can also run the GenAI server using Docker. Make sure you have Docker installed and running.
Also make sure to have the Weaviate instance running. Then, create a Docker network, add the Weaviate container to it, and run the GenAI server:

```bash
docker network create genai-net
docker network connect genai-net weaviate-genai-dev
docker build -t genai .
docker run -d --name genai-app --network genai-net -p 8082:8082 -e WEAVIATE_HOST=weaviate-genai-dev -e WEAVIATE_HTTP_PORT=8080 -e WEAVIATE_GRPC_PORT=50051 genai
```

This will start the GenAI server and test connections to Weaviate. You should see logs indicating that the server is running and connected to Weaviate.