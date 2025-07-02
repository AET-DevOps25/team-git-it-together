#!/usr/bin/env bash
set -e

# ── Configuration ──────────────────────────────────
CONTAINER_NAME="mongo-dev"
VOLUME_NAME="mongo-dev-data"
IMAGE_NAME="mongo:7.0"
DB_NAME="skill_forge_dev"
DB_PORT=27017
# ──────────────────────────────────────────────────

# If container exists, start it; otherwise create + start with volume
if docker ps -aq -f "name=^${CONTAINER_NAME}$" | grep -q .; then
  echo "↻ Starting existing container ${CONTAINER_NAME}..."
  docker start "${CONTAINER_NAME}"
else
  echo "🚀 Creating & starting container ${CONTAINER_NAME}..."
  docker run -d \
    --name "${CONTAINER_NAME}" \
    -p "${DB_PORT}":27017 \
    -e MONGO_INITDB_DATABASE="${DB_NAME}" \
    -e MONGO_INITDB_ROOT_USERNAME="root" \
    -e MONGO_INITDB_ROOT_PASSWORD="password" \
    -v "${VOLUME_NAME}":/data/db \
    "${IMAGE_NAME}"
fi

echo "✅ MongoDB is running at mongodb://localhost:${DB_PORT}/${DB_NAME}"
