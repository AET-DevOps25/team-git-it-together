#!/usr/bin/env bash
set -e

CONTAINER_NAME="mongo-dev"
VOLUME_NAME="mongo-dev-data"

echo "🔄 Resetting MongoDB development environment..."

# Stop & remove container if exists
if docker ps -aq -f "name=^${CONTAINER_NAME}$" | grep -q .; then
  docker stop "${CONTAINER_NAME}" 2>/dev/null || true
  docker rm "${CONTAINER_NAME}"
  echo "✔ Removed container ${CONTAINER_NAME}"
else
  echo "ℹ️  No container ${CONTAINER_NAME} to remove."
fi

# Remove volume if exists
if docker volume ls -q -f "name=^${VOLUME_NAME}$" | grep -q .; then
  docker volume rm "${VOLUME_NAME}"
  echo "✔ Removed volume ${VOLUME_NAME}"
else
  echo "ℹ️  No volume ${VOLUME_NAME} to remove."
fi

echo "✅ Reset complete."
