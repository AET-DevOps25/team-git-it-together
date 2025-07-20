# --- Stage 1: Builder ---
FROM python:3.12-slim AS builder

# Install build tools
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /build

# Install dependencies into a local dir
COPY requirements.txt ./
RUN pip install --upgrade pip && pip install --user --no-cache-dir -r requirements.txt
RUN pip install --no-cache-dir sentence_transformers

# Copy app code (but not user)
COPY . .

# --- Stage 2: Final runtime image ---
FROM python:3.12-slim AS runtime

# Install curl & the new embeddings lib
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && pip install --no-cache-dir sentence_transformers langchain-huggingface \
    && rm -rf /var/lib/apt/lists/*

# Non-root user
RUN useradd --create-home appuser
USER appuser
WORKDIR /home/appuser/app

# Build-time config
ARG GENAI_PORT=8888
ARG GENAI_APP_NAME="SkillForce GenAI"
ARG GENAI_APP_VERSION=0.0.1
ARG UVICORN_WORKERS=2
ARG CORS_ALLOW_ORIGINS=*


# Runtime config
ENV GENAI_PORT=$GENAI_PORT
ENV GENAI_APP_NAME=$GENAI_APP_NAME
ENV GENAI_APP_VERSION=$GENAI_APP_VERSION
ENV UVICORN_WORKERS=$UVICORN_WORKERS
ENV CORS_ALLOW_ORIGINS=$CORS_ALLOW_ORIGINS

EXPOSE $GENAI_PORT

# Copy Python dependencies from builder
COPY --from=builder --chown=appuser /build /home/appuser/app
COPY --from=builder /root/.local /home/appuser/.local

# Ensure user local bin is in path
ENV PATH=/home/appuser/.local/bin:$PATH


ENTRYPOINT ["sh", "-c", "uvicorn src.main:app --host 0.0.0.0 --port ${GENAI_PORT}"]
