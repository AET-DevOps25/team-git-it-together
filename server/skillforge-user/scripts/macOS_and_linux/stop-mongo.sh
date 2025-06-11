#!/usr/bin/env bash
set -e

CONTAINER_NAME="mongo-dev"

if docker ps -q -f "name=^${CONTAINER_NAME}$" | grep -q .; then
  echo "🛑 Stopping container ${CONTAINER_NAME}..."
  docker stop "${CONTAINER_NAME}"
else
  echo "ℹ️  Container ${CONTAINER_NAME} is not running."
fi