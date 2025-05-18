#!/usr/bin/env bash
set -e

# ── Configuration ──────────────────────────────────
CONTAINER_NAME="mongo-dev"
VOLUME_NAME="mongo-dev-data"
IMAGE_NAME="mongo:7.0"
DB_NAME="skill_forge_dev"
# ──────────────────────────────────────────────────

# If container exists, start it; otherwise create + start with volume
if docker ps -aq -f "name=^${CONTAINER_NAME}$" | grep -q .; then
  echo "↻ Starting existing container ${CONTAINER_NAME}..."
  docker start "${CONTAINER_NAME}"
else
  echo "🚀 Creating & starting container ${CONTAINER_NAME}..."
  docker run -d \
    --name "${CONTAINER_NAME}" \
    -p 27017:27017 \
    -e MONGO_INITDB_DATABASE="${DB_NAME}" \
    -e MONGO_INITDB_ROOT_USERNAME="root" \
    -e MONGO_INITDB_ROOT_PASSWORD="root" \
    -v "${VOLUME_NAME}":/data/db \
    "${IMAGE_NAME}"
fi

echo "✅ MongoDB is running at mongodb://localhost:27017/${DB_NAME}"
